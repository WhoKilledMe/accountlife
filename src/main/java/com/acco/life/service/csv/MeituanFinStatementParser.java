package com.acco.life.service.csv;

import com.acco.life.dto.FileTransactionDto;
import com.acco.life.dto.FileTransactionMeituan;
import com.acco.life.entity.fin.FinStatement;
import com.acco.life.enums.TransactionSourceType;
import com.acco.life.enums.fin.FlowDirection;
import com.acco.life.service.AiTransactionCategoryService;
import com.acco.life.util.CsvUtil;
import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * 美团账单 CSV 解析器
 *
 * @author wensen.zhang
 * @version V2.0.0
 */
@Slf4j
@Component
public class MeituanFinStatementParser extends AbstractFinStatementParser implements FinStatementCsvParser {

    @Override
    public boolean supports(TransactionSourceType sourceType) {
        return sourceType == TransactionSourceType.PLATFORM;
    }

    @Override
    public String getPlatformCode() {
        return "MEITUAN";
    }

    @Override
    public String getParserVersion() {
        return "2.0.0";
    }

    @Override
    public List<? extends FileTransactionDto> parse(InputStream inputStream) throws Exception {
        InputStream skippedStream = CsvUtil.skipLines(inputStream, 19);
        CsvSchema schema = csvMapper.schemaFor(FileTransactionMeituan.class).withHeader();
        MappingIterator<FileTransactionMeituan> it = csvMapper.readerFor(FileTransactionMeituan.class)
                .with(schema)
                .readValues(skippedStream);
        return it.readAll();
    }

    @Override
    public List<FinStatement> buildStatements(List<? extends FileTransactionDto> dtos, Long userId, Long fileId,
                                              AiTransactionCategoryService categoryService) {
        List<FinStatement> statements = new ArrayList<>();
        
        for (FileTransactionDto dto : dtos) {
            if (!(dto instanceof FileTransactionMeituan mt)) {
                continue;
            }
            
            try {
                FinStatement stmt = buildStatement(mt, userId, fileId, categoryService);
                statements.add(stmt);
            } catch (Exception e) {
                log.error("解析美团账单行失败: {}", mt, e);
            }
        }
        
        return statements;
    }

    private FinStatement buildStatement(FileTransactionMeituan mt, Long userId, Long fileId,
                                       AiTransactionCategoryService categoryService) throws Exception {
        FinStatement stmt = new FinStatement();
        
        setBasicFields(stmt, userId, fileId);
        setOrderInfo(stmt, mt);
        setHashAndRawData(stmt, mt, fileId);
        setTimeAndAmount(stmt, mt);
        setDirection(stmt);
        setMerchantInfo(stmt, mt);
        inferCategoryIfNeeded(stmt, mt, userId, categoryService);
        
        return stmt;
    }

    private void setBasicFields(FinStatement stmt, Long userId, Long fileId) {
        setCommonFields(stmt, userId, fileId, getPlatformCode());
        stmt.setSourceType("PLATFORM_ORDER");
    }

    private void setOrderInfo(FinStatement stmt, FileTransactionMeituan mt) {
        stmt.setOutTradeNo(mt.getTradeNo());
    }

    private void setHashAndRawData(FinStatement stmt, FileTransactionMeituan mt, Long fileId) throws Exception {
        String rowHash = calculateRowHash(fileId, 
                mt.getTradeNo(), 
                mt.getSuccessTime(), 
                mt.getPaidAmount());
        stmt.setRawRowHash(rowHash);
        stmt.setRawData(objectMapper.writeValueAsString(mt));
    }

    private void setTimeAndAmount(FinStatement stmt, FileTransactionMeituan mt) {
        String timeStr = mt.getSuccessTime() != null ? mt.getSuccessTime() : mt.getCreatedTime();
        stmt.setStmtTime(parseDateTime(timeStr));
        BigDecimal amount = parseAmount(mt.getPaidAmount());
        stmt.setAmount(amount.abs());
    }

    private void setDirection(FinStatement stmt) {
        stmt.setDirection(FlowDirection.OUT.getCode());
    }

    private void setMerchantInfo(FinStatement stmt, FileTransactionMeituan mt) {
        stmt.setCounterparty(extractMerchantName(mt));
        stmt.setDescription(buildDescription(mt));
    }

    private void inferCategoryIfNeeded(FinStatement stmt, FileTransactionMeituan mt, Long userId,
                                      AiTransactionCategoryService categoryService) {
        if (categoryService == null) {
            return;
        }
        
        String counterparty = stmt.getCounterparty();
        String description = stmt.getDescription() != null ? stmt.getDescription() : "";
        String amountStr = mt.getPaidAmount() != null ? mt.getPaidAmount() : "0";
        String cleanAmount = formatAmountForCategory(amountStr, "支出");
        
        inferCategory(stmt, counterparty, description, cleanAmount, userId, categoryService);
    }

    private String extractMerchantName(FileTransactionMeituan mt) {
        if (mt.getOrderTitle() != null && !mt.getOrderTitle().isEmpty()) {
            return mt.getOrderTitle();
        }
        return null;
    }

    private String buildDescription(FileTransactionMeituan mt) {
        StringBuilder sb = new StringBuilder();
        
        appendIfNotEmpty(sb, mt.getOrderTitle(), null);
        appendIfNotEmpty(sb, mt.getTransactionType(), " - ");
        appendIfNotEmpty(sb, mt.getRemark(), " (", ")");
        
        return sb.toString();
    }

    private void appendIfNotEmpty(StringBuilder sb, String value, String prefix) {
        appendIfNotEmpty(sb, value, prefix, null);
    }

    private void appendIfNotEmpty(StringBuilder sb, String value, String prefix, String suffix) {
        if (value == null || value.isEmpty()) {
            return;
        }
        
        if (sb.length() > 0 && prefix != null) {
            sb.append(prefix);
        }
        
        sb.append(value);
        
        if (suffix != null && !sb.toString().endsWith(suffix)) {
            sb.append(suffix);
        }
    }
}
