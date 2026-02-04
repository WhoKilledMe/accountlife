package com.acco.life.service.csv;

import com.acco.life.dto.FileTransactionDto;
import com.acco.life.dto.FileTransactionNingBoBank;
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
 * 宁波银行信用卡账单 CSV 解析器（新版，返回 FinStatement）
 *
 * @author wensen.zhang
 * @version V2.0.0
 */
@Slf4j
@Component
public class NingboFinStatementParser implements FinStatementCsvParser {

    private final CsvMapper csvMapper = new CsvMapper();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public boolean supports(TransactionSourceType sourceType) {
        return sourceType == TransactionSourceType.NING_BO_CREDIT;
    }

    @Override
    public String getPlatformCode() {
        return "NINGBO_BANK";
    }

    @Override
    public String getParserVersion() {
        return "2.0.0";
    }

    @Override
    public List<? extends FileTransactionDto> parse(InputStream inputStream) throws Exception {
        CsvSchema schema = csvMapper.schemaFor(FileTransactionNingBoBank.class).withHeader();
        MappingIterator<FileTransactionNingBoBank> it = csvMapper.readerFor(FileTransactionNingBoBank.class)
                .with(schema)
                .readValues(inputStream);
        return it.readAll();
    }

    @Override
    public List<FinStatement> buildStatements(List<? extends FileTransactionDto> dtos, Long userId, Long fileId,
                                              AiTransactionCategoryService categoryService) {
        List<FinStatement> statements = new ArrayList<>();
        
        for (FileTransactionDto dto : dtos) {
            if (!(dto instanceof FileTransactionNingBoBank nb)) {
                continue;
            }
            
            try {
                FinStatement stmt = new FinStatement();
                stmt.setFileId(fileId);
                stmt.setUserId(userId);
                stmt.setPlatformCode(getPlatformCode());
                stmt.setSourceType("CREDIT_CARD_STATEMENT");
                
                // 计算行级 Hash
                String transactionDate = nb.getTransactionDate() != null ? nb.getTransactionDate() : "";
                String rawContent = transactionDate + nb.getTransactionAmount() + nb.getTransactionSummary();
                String rowHash = DigestUtils.md5DigestAsHex((fileId + rawContent).getBytes(StandardCharsets.UTF_8));
                stmt.setRawRowHash(rowHash);
                
                // 存储原始 JSON
                stmt.setRawData(objectMapper.writeValueAsString(nb));
                
                // 解析时间 - 优先使用交易日期，如果没有则使用记账日期
                String dateStr = nb.getTransactionDate() != null ? nb.getTransactionDate() : nb.getAccountingDate();
                stmt.setStmtTime(parseDateTime(dateStr));
                
                // 解析金额和方向
                BigDecimal amount = parseAmount(nb.getTransactionAmount());
                stmt.setAmount(amount.abs());
                stmt.setDirection(amount.compareTo(BigDecimal.ZERO) >= 0 ? FlowDirection.OUT.getCode() : FlowDirection.IN.getCode());
                
                // 设置摘要信息
                stmt.setDescription(nb.getTransactionSummary());
                stmt.setCounterparty(extractCounterparty(nb.getTransactionSummary()));
                
                // 设置账户引用（卡号尾号等）- 从摘要中提取或设为null
                stmt.setAccountRef(null);
                
                // AI分类推断
                if (categoryService != null) {
                    try {
                        String description = nb.getTransactionSummary() != null ? nb.getTransactionSummary() : "";
                        String amountStr = nb.getTransactionAmount() != null ? nb.getTransactionAmount() : "0";
                        
                        // 使用新方法：传入counterparty + description
                        var category = categoryService.inferTransactionCategory(stmt.getCounterparty(), description, amountStr, userId).block();
                        if (category != null && category.getId() != null) {
                            stmt.setCategoryId(category.getId());
                            log.debug("宁波银行账单分类推断成功: {} [{}] -> {}", stmt.getCounterparty(), description, category.getName());
                        }
                    } catch (Exception e) {
                        log.warn("宁波银行账单分类推断失败: {} - {}", stmt.getCounterparty(), stmt.getDescription(), e);
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
                log.error("解析宁波银行账单行失败: {}", nb, e);
            }
        }
        
        return statements;
    }

    private LocalDateTime parseDateTime(String dateTimeStr) {
        if (dateTimeStr == null || dateTimeStr.isEmpty()) {
            return LocalDateTime.now();
        }
        try {
            // 假设格式为 yyyy-MM-dd HH:mm:ss 或 yyyy/MM/dd HH:mm:ss
            String normalized = dateTimeStr.replace("/", "-");
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
            // 移除货币符号和千位分隔符
            String cleaned = amountStr.replaceAll("[¥￥,，]", "").trim();
            return new BigDecimal(cleaned);
        } catch (Exception e) {
            log.warn("解析金额失败: {}", amountStr);
            return BigDecimal.ZERO;
        }
    }

    private String extractCounterparty(String summary) {
        if (summary == null || summary.isEmpty()) {
            return null;
        }
        // 简单提取：取前20个字符作为对手方
        return summary.length() > 20 ? summary.substring(0, 20) : summary;
    }
}
