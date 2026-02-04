package com.acco.life.service.csv;

import com.acco.life.dto.FileTransactionDto;
import com.acco.life.dto.FileTransactionJingdong;
import com.acco.life.entity.fin.FinStatement;
import com.acco.life.enums.TransactionSourceType;
import com.acco.life.enums.fin.FlowDirection;
import com.acco.life.enums.fin.StatementStatus;
import com.acco.life.service.fin.FinStatementMappingService;
import com.acco.life.service.AiTransactionCategoryService;
import com.acco.life.util.CsvUtil;
import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.DigestUtils;

import java.io.InputStream;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 京东账单 CSV 解析器（新版，返回 FinStatement）
 *
 * @author wensen.zhang
 * @version V2.0.0
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class JingdongFinStatementParser implements FinStatementCsvParser {

    private final CsvMapper csvMapper = new CsvMapper();
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final FinStatementMappingService mappingService;

    @Override
    public boolean supports(TransactionSourceType sourceType) {
        return sourceType == TransactionSourceType.JINGDONG;
    }

    @Override
    public String getPlatformCode() {
        return "JD";
    }

    @Override
    public String getParserVersion() {
        return "2.0.0";
    }

    @Override
    public List<? extends FileTransactionDto> parse(InputStream inputStream) throws Exception {
        // 京东账单前面有多行导出说明，从真正的表头行“交易时间,商户名称,...”开始解析
        InputStream processedStream = CsvUtil.skipUntilHeader(inputStream, "交易时间");

        CsvSchema schema = csvMapper.schemaFor(FileTransactionJingdong.class).withHeader();
        MappingIterator<FileTransactionJingdong> it = csvMapper.readerFor(FileTransactionJingdong.class)
                .with(schema)
                .readValues(processedStream);
        return it.readAll();
    }

    @Override
    public List<FinStatement> buildStatements(List<? extends FileTransactionDto> dtos, Long userId, Long fileId,
                                              AiTransactionCategoryService categoryService) {
        List<FinStatement> statements = new ArrayList<>();

        for (FileTransactionDto dto : dtos) {
            if (!(dto instanceof FileTransactionJingdong jd)) {
                continue;
            }

            // 1. 「不计收支」只保留京东白条还款，其他记录跳过
            if ("不计收支".equals(jd.getIncomeOrExpense()) && !isBaitiaoRepay(jd)) {
                log.debug("京东账单跳过不计收支记录: {}", jd);
                continue;
            }

            try {

                FinStatement stmt = new FinStatement();
                stmt.setFileId(fileId);
                stmt.setUserId(userId);
                stmt.setPlatformCode(getPlatformCode());
                stmt.setSourceType("PLATFORM_ORDER");
                
                // 设置订单号 - 优先使用交易订单号，如果没有则使用商家订单号
                String orderNo = jd.getTradeOrderNo() != null && !jd.getTradeOrderNo().isEmpty() 
                        ? jd.getTradeOrderNo() 
                        : jd.getMerchantOrderNo();
                stmt.setOutTradeNo(orderNo);
                
                // 计算行级 Hash
                String tradeOrderNo = jd.getTradeOrderNo() != null ? jd.getTradeOrderNo() : "";
                String merchantOrderNo = jd.getMerchantOrderNo() != null ? jd.getMerchantOrderNo() : "";
                String tradeTime = jd.getTradeTime() != null ? jd.getTradeTime() : "";
                String amount = jd.getAmount() != null ? jd.getAmount() : "";
                String rawContent = tradeOrderNo + merchantOrderNo + tradeTime + amount;
                String rowHash = DigestUtils.md5DigestAsHex((fileId + rawContent).getBytes(StandardCharsets.UTF_8));
                stmt.setRawRowHash(rowHash);
                
                // 存储原始 JSON
                stmt.setRawData(objectMapper.writeValueAsString(jd));
                
                // 解析时间
                stmt.setStmtTime(parseDateTime(jd.getTradeTime()));
                
                // 解析金额（统一存正数）
                BigDecimal amountValue = parseAmount(jd.getAmount());
                stmt.setAmount(amountValue.abs());
                
                // 2. 根据收/支字段判断方向
                String direction;
                if ("收入".equals(jd.getIncomeOrExpense())) {
                    direction = FlowDirection.IN.getCode();
                } else if ("支出".equals(jd.getIncomeOrExpense())) {
                    direction = FlowDirection.OUT.getCode();
                } else if ("不计收支".equals(jd.getIncomeOrExpense())) {
                    // 仅当 isBaitiaoRepay=true 时才会进入这里，视为还款支出
                    direction = FlowDirection.OUT.getCode();
                } else {
                    // 默认支出
                    direction = FlowDirection.OUT.getCode();
                }
                stmt.setDirection(direction);
                
                // 设置商户信息
                stmt.setCounterparty(jd.getMerchantName());
                stmt.setDescription(buildDescription(jd));
                
                // 5. 设置账户引用 - 使用配置化映射服务做归一化
                //    - 微信支付          -> WECHAT_PAY
                //    - 余额              -> JD_BALANCE
                //    - 京东白条          -> JD_BAITIAO
                //    - 京东小金库/京东支付 -> JD_PAY
                //    - 招商/农行等银行卡  -> 提取卡号尾号（括号内数字，如 6527）
                String accountRef = extractAccountRef(jd.getPaymentMethod());
                stmt.setAccountRef(accountRef);
                
                // 4. category_id 使用交易分类 + AI 推断（结合counterparty和description）
                if (categoryService != null) {
                    try {
                        String counterparty = stmt.getCounterparty();  // 商户名称（如：京东外卖）
                        String description = jd.getTradeDescription() != null ?  jd.getTradeDescription() : "";
                        String amountStr = jd.getAmount() != null ? jd.getAmount() : "0";
                        // 根据方向调整金额符号
                        if ("支出".equals(jd.getIncomeOrExpense())) {
                            amountStr = "-" + amountStr.replaceAll("[¥￥,，]", "").trim();
                        } else {
                            amountStr = amountStr.replaceAll("[¥￥,，]", "").trim();
                        }
                        
                        // 使用新方法：传入counterparty + description
                        var category = categoryService.inferTransactionCategory(counterparty, description, amountStr, userId).block();
                        if (category != null && category.getId() != null) {
                            stmt.setCategoryId(category.getId());
                            log.debug("京东账单分类推断成功: {} [{}] -> {}", counterparty, description, category.getName());
                        }
                    } catch (Exception e) {
                        log.warn("京东账单分类推断失败: {} - {}", stmt.getCounterparty(), stmt.getDescription(), e);
                    }
                }
                
                // 设置解析器信息
                stmt.setParserVersion(getParserVersion());
                stmt.setParsedAt(LocalDateTime.now());
                stmt.setRetryCount(0);
                stmt.setStatus(StatementStatus.PARSED.getCode());
                stmt.setCreatedAt(LocalDateTime.now());
                
                statements.add(stmt);
            } catch (Exception e) {
                log.error("解析京东账单行失败: {}", jd, e);
            }
        }
        
        return statements;
    }

    private LocalDateTime parseDateTime(String dateTimeStr) {
        if (dateTimeStr == null || dateTimeStr.isEmpty()) {
            return LocalDateTime.now();
        }
        try {
            String normalized = dateTimeStr.replace("/", "-").trim();
            if (normalized.length() == 10) {
                normalized += " 00:00:00";
            }
            return LocalDateTime.parse(normalized, java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        } catch (Exception e) {
            log.warn("解析时间失败: {}", dateTimeStr);
            return LocalDateTime.now();
        }
    }

    private BigDecimal parseAmount(String amountStr) {
        if (amountStr == null || amountStr.isEmpty()) {
            return BigDecimal.ZERO;
        }
        try {
            String cleaned = amountStr.replaceAll("[¥￥,，]", "").trim();
            return new BigDecimal(cleaned);
        } catch (Exception e) {
            log.warn("解析金额失败: {}", amountStr);
            return BigDecimal.ZERO;
        }
    }

    private String buildDescription(FileTransactionJingdong jd) {
        StringBuilder sb = new StringBuilder();
        if (jd.getTradeDescription() != null && !jd.getTradeDescription().isEmpty()) {
            sb.append(jd.getTradeDescription());
        }
        if (jd.getTradeCategory() != null && !jd.getTradeCategory().isEmpty()) {
            if (sb.length() > 0) {
                sb.append(" - ");
            }
            sb.append(jd.getTradeCategory());
        }
        if (jd.getRemark() != null && !jd.getRemark().isEmpty()) {
            if (sb.length() > 0) {
                sb.append(" (");
            }
            sb.append(jd.getRemark());
            if (sb.length() > 0 && sb.charAt(sb.length() - 1) != ')') {
                sb.append(")");
            }
        }
        return sb.toString();
    }

    /**
     * 判断是否为京东白条还款记录
     * 典型特征：
     * - 收/支 = 不计收支
     * - 交易分类 = 白条，或
     * - 商户名称包含「京东白条」，或
     * - 交易说明/备注中包含「白条主动还款」
     */
    private boolean isBaitiaoRepay(FileTransactionJingdong jd) {
        String category = jd.getTradeCategory() != null ? jd.getTradeCategory() : "";
        String merchantName = jd.getMerchantName() != null ? jd.getMerchantName() : "";
        String desc = jd.getTradeDescription() != null ? jd.getTradeDescription() : "";
        String remark = jd.getRemark() != null ? jd.getRemark() : "";

        if (category.contains("白条")) {
            return true;
        }
        if (merchantName.contains("京东白条")) {
            return true;
        }
        if (desc.contains("白条主动还款") || remark.contains("白条主动还款")) {
            return true;
        }
        return false;
    }

    /**
     * 从收/付款方式中提取账户引用，先走配置表规则，再做通用兜底逻辑
     */
    private String extractAccountRef(String paymentMethod) {
        if (paymentMethod == null || paymentMethod.isEmpty()) {
            return null;
        }
        String pm = paymentMethod.trim();

        // 优先使用配置化映射规则
        try {
            String mapped = mappingService.mapAccountRef(getPlatformCode(), pm).block();
            if (mapped != null && !mapped.isEmpty() && !pm.equals(mapped)) {
                return mapped;
            }
        } catch (Exception e) {
            log.warn("京东账单 account_ref 映射规则执行失败，回退到本地逻辑: {}", pm, e);
        }

        // 兜底逻辑：兼容未配置规则的情况
        // 招商 / 农行等银行卡：尝试提取卡号尾号，如 "招商银行储蓄卡(6527)" -> "6527"
        int start = pm.indexOf('(');
        int end = pm.indexOf(')');
        if (start > 0 && end > start) {
            String tail = pm.substring(start + 1, end).trim();
            if (!tail.isEmpty()) {
                return tail;
            }
        }

        // 其他情况：直接返回原始支付方式文本
        return pm;
    }
}
