package com.acco.life.service.csv;

import com.acco.life.dto.FileTransactionDto;
import com.acco.life.dto.FileTransactionLabelDetail;
import com.acco.life.entity.fin.FinStatement;
import com.acco.life.enums.TransactionSourceType;
import com.acco.life.enums.fin.FlowDirection;
import com.acco.life.enums.fin.StatementStatus;
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
 * LabelDetail 账单 CSV 解析器（新版，返回 FinStatement）
 *
 * @author wensen.zhang
 * @version V2.0.0
 */
@Slf4j
@Component
public class LabelDetailFinStatementParser implements FinStatementCsvParser {

    private final CsvMapper csvMapper = new CsvMapper();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public boolean supports(TransactionSourceType sourceType) {
        return sourceType == TransactionSourceType.LABEL_DETAIL;
    }

    @Override
    public String getPlatformCode() {
        return "LABEL_DETAIL";
    }

    @Override
    public String getParserVersion() {
        return "2.0.0";
    }

    @Override
    public List<? extends FileTransactionDto> parse(InputStream inputStream) throws Exception {
        CsvSchema schema = csvMapper.schemaFor(FileTransactionLabelDetail.class).withHeader();
        MappingIterator<FileTransactionLabelDetail> it = csvMapper.readerFor(FileTransactionLabelDetail.class)
                .with(schema)
                .readValues(inputStream);
        return it.readAll();
    }

    @Override
    public List<FinStatement> buildStatements(List<? extends FileTransactionDto> dtos, Long userId, Long fileId) {
        List<FinStatement> statements = new ArrayList<>();
        
        for (FileTransactionDto dto : dtos) {
            if (!(dto instanceof FileTransactionLabelDetail ld)) {
                continue;
            }
            
            try {
                FinStatement stmt = new FinStatement();
                stmt.setFileId(fileId);
                stmt.setUserId(userId);
                stmt.setPlatformCode(getPlatformCode());
                stmt.setSourceType("BANK_STATEMENT");
                
                // 计算行级 Hash
                String rawContent = ld.getTransactionTime() + ld.getTransactionAmount() + ld.getTransactionSummary();
                String rowHash = DigestUtils.md5DigestAsHex((fileId + rawContent).getBytes(StandardCharsets.UTF_8));
                stmt.setRawRowHash(rowHash);
                
                // 存储原始 JSON
                stmt.setRawData(objectMapper.writeValueAsString(ld));
                
                // 解析时间
                stmt.setStmtTime(parseDateTime(ld.getTransactionTime()));
                
                // 解析金额和方向
                BigDecimal amount = parseAmount(ld.getTransactionAmount());
                stmt.setAmount(amount.abs());
                
                // 根据交易类型判断方向
                String direction = determineDirection(ld.getTransactionType(), amount);
                stmt.setDirection(direction);
                
                // 设置摘要信息
                stmt.setDescription(ld.getTransactionSummary());
                stmt.setCounterparty(ld.getCounterparty());
                
                // 设置账户引用
                stmt.setAccountRef(ld.getAccountNumber());
                
                // 设置解析器信息
                stmt.setParserVersion(getParserVersion());
                stmt.setParsedAt(LocalDateTime.now());
                stmt.setRetryCount(0);
                stmt.setStatus(StatementStatus.PARSED.getCode());
                stmt.setCreatedAt(LocalDateTime.now());
                
                statements.add(stmt);
            } catch (Exception e) {
                log.error("解析LabelDetail账单行失败: {}", ld, e);
            }
        }
        
        return statements;
    }

    private LocalDateTime parseDateTime(String dateTimeStr) {
        if (dateTimeStr == null || dateTimeStr.isEmpty()) {
            return LocalDateTime.now();
        }
        try {
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
            String cleaned = amountStr.replaceAll("[¥￥,，]", "").trim();
            return new BigDecimal(cleaned);
        } catch (Exception e) {
            log.warn("解析金额失败: {}", amountStr);
            return BigDecimal.ZERO;
        }
    }

    private String determineDirection(String transactionType, BigDecimal amount) {
        if (transactionType != null) {
            String type = transactionType.toLowerCase();
            if (type.contains("收入") || type.contains("入账") || type.contains("转入")) {
                return FlowDirection.IN.getCode();
            }
            if (type.contains("支出") || type.contains("消费") || type.contains("转出")) {
                return FlowDirection.OUT.getCode();
            }
        }
        // 根据金额正负判断
        return amount.compareTo(BigDecimal.ZERO) >= 0 ? FlowDirection.OUT.getCode() : FlowDirection.IN.getCode();
    }
}
