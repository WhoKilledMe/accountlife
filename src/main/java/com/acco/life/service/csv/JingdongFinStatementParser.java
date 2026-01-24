package com.acco.life.service.csv;

import com.acco.life.dto.FileTransactionDto;
import com.acco.life.dto.FileTransactionJingdong;
import com.acco.life.entity.fin.FinStatement;
import com.acco.life.enums.TransactionSourceType;
import com.acco.life.enums.fin.FlowDirection;
import com.acco.life.enums.fin.StatementStatus;
import com.acco.life.service.AiTransactionCategoryService;
import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
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
public class JingdongFinStatementParser implements FinStatementCsvParser {

    private final CsvMapper csvMapper = new CsvMapper();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public boolean supports(TransactionSourceType sourceType) {
        return sourceType == TransactionSourceType.JINGDONG;
    }

    @Override
    public String getPlatformCode() {
        return "JINGDONG";
    }

    @Override
    public String getParserVersion() {
        return "2.0.0";
    }

    @Override
    public List<? extends FileTransactionDto> parse(InputStream inputStream) throws Exception {
        // 京东账单通常第一行是表头，直接解析
        CsvSchema schema = csvMapper.schemaFor(FileTransactionJingdong.class).withHeader();
        MappingIterator<FileTransactionJingdong> it = csvMapper.readerFor(FileTransactionJingdong.class)
                .with(schema)
                .readValues(inputStream);
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
            
            try {
                // 只处理交易成功的记录
                if (!"交易成功".equals(jd.getTradeStatus())) {
                    log.debug("跳过非成功交易: {}", jd.getTradeOrderNo());
                    continue;
                }
                
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
                
                // 解析金额和方向
                BigDecimal amountValue = parseAmount(jd.getAmount());
                stmt.setAmount(amountValue.abs());
                
                // 根据收/支字段判断方向
                String direction;
                if ("收入".equals(jd.getIncomeOrExpense())) {
                    direction = FlowDirection.IN.getCode();
                } else if ("支出".equals(jd.getIncomeOrExpense())) {
                    direction = FlowDirection.OUT.getCode();
                } else {
                    // 默认支出
                    direction = FlowDirection.OUT.getCode();
                }
                stmt.setDirection(direction);
                
                // 设置商户信息
                stmt.setCounterparty(jd.getMerchantName());
                stmt.setDescription(buildDescription(jd));
                
                // 设置账户引用 - 从支付方式中提取（如"微信支付"）
                stmt.setAccountRef(extractAccountRef(jd.getPaymentMethod()));
                
                // AI分类推断
                if (categoryService != null) {
                    try {
                        String description = stmt.getDescription() != null ? stmt.getDescription() : "";
                        String amountStr = jd.getAmount() != null ? jd.getAmount() : "0";
                        // 根据方向调整金额符号
                        if ("支出".equals(jd.getIncomeOrExpense())) {
                            amountStr = "-" + amountStr.replaceAll("[¥￥,，]", "").trim();
                        } else {
                            amountStr = amountStr.replaceAll("[¥￥,，]", "").trim();
                        }
                        
                        var category = categoryService.inferTransactionCategory(description, amountStr, userId).block();
                        if (category != null && category.getId() != null) {
                            stmt.setCategoryId(category.getId());
                            log.debug("京东账单分类推断成功: {} -> {}", description, category.getName());
                        }
                    } catch (Exception e) {
                        log.warn("京东账单分类推断失败: {}", stmt.getDescription(), e);
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

    private String extractAccountRef(String paymentMethod) {
        if (paymentMethod == null || paymentMethod.isEmpty()) {
            return null;
        }
        // 提取支付方式作为账户引用
        return paymentMethod;
    }
}
