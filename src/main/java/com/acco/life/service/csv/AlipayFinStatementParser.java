package com.acco.life.service.csv;

import com.acco.life.dto.FileTransactionDto;
import com.acco.life.dto.FileTransactionAlipay;
import com.acco.life.entity.fin.FinStatement;
import com.acco.life.enums.TransactionSourceType;
import com.acco.life.enums.fin.FlowDirection;
import com.acco.life.enums.fin.StatementStatus;
import com.acco.life.service.AiTransactionCategoryService;
import com.acco.life.service.fin.FinStatementMappingService;
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
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 支付宝账单 CSV 解析器（新版，返回 FinStatement）
 *
 * @author wensen.zhang
 * @version V2.0.0
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class AlipayFinStatementParser implements FinStatementCsvParser {

    private final CsvMapper csvMapper = new CsvMapper();
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final FinStatementMappingService mappingService;

    @Override
    public boolean supports(TransactionSourceType sourceType) {
        return sourceType == TransactionSourceType.ALIPAY;
    }

    @Override
    public String getPlatformCode() {
        return "ALIPAY";
    }

    @Override
    public String getParserVersion() {
        return "2.0.0";
    }

    @Override
    public List<? extends FileTransactionDto> parse(InputStream inputStream) throws Exception {
        // 支付宝账单前24行为元数据，需要跳过
        // 第25行为表头，第26行开始是数据
        // 支付宝CSV文件使用GBK编码，需要指定字符集
        InputStream skippedStream = CsvUtil.skipLines(inputStream, 24, Charset.forName("GBK"));
        
        CsvSchema schema = csvMapper.schemaFor(FileTransactionAlipay.class).withHeader();
        MappingIterator<FileTransactionAlipay> it = csvMapper.readerFor(FileTransactionAlipay.class)
                .with(schema)
                .readValues(skippedStream);
        return it.readAll();
    }

    @Override
    public List<FinStatement> buildStatements(List<? extends FileTransactionDto> dtos, Long userId, Long fileId,
                                              AiTransactionCategoryService categoryService) {
        List<FinStatement> statements = new ArrayList<>();
        
        for (FileTransactionDto dto : dtos) {
            if (!(dto instanceof FileTransactionAlipay alipay)) {
                continue;
            }
            
            try {
                // 过滤"不计收支"类型的交易（如余额宝收益、自动转入等）
                if ("不计收支".equals(alipay.getIncomeOrExpense())) {
                    log.debug("跳过不计收支交易: {}", alipay.getProductDescription());
                    continue;
                }
                
                FinStatement stmt = new FinStatement();
                stmt.setFileId(fileId);
                stmt.setUserId(userId);
                stmt.setPlatformCode(getPlatformCode());
                stmt.setSourceType("PLATFORM_ORDER");
                
                // 设置订单号 - 使用交易订单号
                stmt.setOutTradeNo(alipay.getTradeOrderNo());
                
                // 计算行级 Hash
                String tradeOrderNo = alipay.getTradeOrderNo() != null ? alipay.getTradeOrderNo() : "";
                String tradeTime = alipay.getTradeTime() != null ? alipay.getTradeTime() : "";
                String amount = alipay.getAmount() != null ? alipay.getAmount() : "";
                String rawContent = tradeOrderNo + tradeTime + amount;
                String rowHash = DigestUtils.md5DigestAsHex((fileId + rawContent).getBytes(StandardCharsets.UTF_8));
                stmt.setRawRowHash(rowHash);
                
                // 存储原始 JSON
                stmt.setRawData(objectMapper.writeValueAsString(alipay));
                
                // 解析时间
                stmt.setStmtTime(parseDateTime(alipay.getTradeTime()));
                
                // 解析金额和方向
                BigDecimal amountValue = parseAmount(alipay.getAmount());
                stmt.setAmount(amountValue.abs());
                
                // 根据收/支字段判断方向
                String direction;
                if ("收入".equals(alipay.getIncomeOrExpense())) {
                    direction = FlowDirection.IN.getCode();
                } else if ("支出".equals(alipay.getIncomeOrExpense())) {
                    direction = FlowDirection.OUT.getCode();
                } else {
                    // 默认根据金额正负判断（虽然理论上不应该到这里）
                    direction = amountValue.compareTo(BigDecimal.ZERO) >= 0 
                            ? FlowDirection.OUT.getCode() 
                            : FlowDirection.IN.getCode();
                }
                stmt.setDirection(direction);
                
                // 设置商户信息
                stmt.setCounterparty(alipay.getCounterparty());
                stmt.setDescription(buildDescription(alipay));
                
                // 设置账户引用 - 先通过配置化映射，再从支付方式中提取（如"宁波银行信用卡(4573)"）
                stmt.setAccountRef(extractAccountRef(alipay.getPaymentMethod()));
                
                // AI分类推断
                if (categoryService != null) {
                    try {
                        String description = stmt.getDescription() != null ? stmt.getDescription() : "";
                        String amountStr = alipay.getAmount() != null ? alipay.getAmount() : "0";
                        // 根据方向调整金额符号
                        if ("支出".equals(alipay.getIncomeOrExpense())) {
                            amountStr = "-" + amountStr.replaceAll("[¥￥,，]", "").trim();
                        } else {
                            amountStr = amountStr.replaceAll("[¥￥,，]", "").trim();
                        }
                        
                        // 使用新方法：传入counterparty + description
                        var category = categoryService.inferTransactionCategory(stmt.getCounterparty(), description, amountStr, userId).block();
                        if (category != null && category.getId() != null) {
                            stmt.setCategoryId(category.getId());
                            log.debug("支付宝账单分类推断成功: {} [{}] -> {}", stmt.getCounterparty(), description, category.getName());
                        }
                    } catch (Exception e) {
                        log.warn("支付宝账单分类推断失败: {} - {}", stmt.getCounterparty(), stmt.getDescription(), e);
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
                log.error("解析支付宝账单行失败: {}", alipay, e);
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

    private String buildDescription(FileTransactionAlipay alipay) {
        StringBuilder sb = new StringBuilder();
        if (alipay.getProductDescription() != null && !alipay.getProductDescription().isEmpty()) {
            sb.append(alipay.getProductDescription());
        }
        if (alipay.getTradeCategory() != null && !alipay.getTradeCategory().isEmpty()) {
            if (sb.length() > 0) {
                sb.append(" - ");
            }
            sb.append(alipay.getTradeCategory());
        }
        if (alipay.getRemark() != null && !alipay.getRemark().isEmpty()) {
            if (sb.length() > 0) {
                sb.append(" (");
            }
            sb.append(alipay.getRemark());
            if (sb.length() > 0 && sb.charAt(sb.length() - 1) != ')') {
                sb.append(")");
            }
        }
        return sb.toString();
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
            log.warn("支付宝账单 account_ref 映射规则执行失败，回退到本地逻辑: {}", pm, e);
        }

        // 兜底逻辑：兼容未配置规则的情况
        // 尝试提取卡号尾号，如"宁波银行信用卡(4573)" -> "4573"
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
