package com.acco.life.service.csv;

import com.acco.life.dto.FileTransactionDto;
import com.acco.life.dto.FileTransactionLabelDetail;
import com.acco.life.entity.fin.FinStatement;
import com.acco.life.enums.TransactionSourceType;
import com.acco.life.service.AiTransactionCategoryService;
import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * LabelDetail 账单 CSV 解析器
 *
 * @author wensen.zhang
 * @version V2.0.0
 */
@Slf4j
@Component
public class LabelDetailFinStatementParser extends AbstractFinStatementParser implements FinStatementCsvParser {

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
    public List<FinStatement> buildStatements(List<? extends FileTransactionDto> dtos, Long userId, Long fileId,
                                              AiTransactionCategoryService categoryService) {
        List<FinStatement> statements = new ArrayList<>();
        
        for (FileTransactionDto dto : dtos) {
            if (!(dto instanceof FileTransactionLabelDetail ld)) {
                continue;
            }
            
            try {
                FinStatement stmt = buildStatement(ld, userId, fileId, categoryService);
                statements.add(stmt);
            } catch (Exception e) {
                log.error("解析LabelDetail账单行失败: {}", ld, e);
            }
        }
        
        return statements;
    }

    private FinStatement buildStatement(FileTransactionLabelDetail ld, Long userId, Long fileId,
                                       AiTransactionCategoryService categoryService) throws Exception {
        FinStatement stmt = new FinStatement();
        
        setBasicFields(stmt, userId, fileId);
        setHashAndRawData(stmt, ld, fileId);
        setTimeAndAmount(stmt, ld);
        setDirection(stmt, ld);
        setSummaryInfo(stmt, ld);
        inferCategoryIfNeeded(stmt, ld, userId, categoryService);
        
        return stmt;
    }

    private void setBasicFields(FinStatement stmt, Long userId, Long fileId) {
        setCommonFields(stmt, userId, fileId, getPlatformCode());
        stmt.setSourceType("BANK_STATEMENT");
    }

    private void setHashAndRawData(FinStatement stmt, FileTransactionLabelDetail ld, Long fileId) throws Exception {
        String rowHash = calculateRowHash(fileId, 
                ld.getTransactionDate(), 
                ld.getTransactionAmount(), 
                ld.getTransactionSummary());
        stmt.setRawRowHash(rowHash);
        stmt.setRawData(objectMapper.writeValueAsString(ld));
    }

    private void setTimeAndAmount(FinStatement stmt, FileTransactionLabelDetail ld) {
        String dateStr = ld.getTransactionDate() != null ? ld.getTransactionDate() : ld.getAccountingDate();
        stmt.setStmtTime(parseDateTime(dateStr));
        BigDecimal amount = parseAmount(ld.getTransactionAmount());
        stmt.setAmount(amount.abs());
    }

    private void setDirection(FinStatement stmt, FileTransactionLabelDetail ld) {
        BigDecimal amount = parseAmount(ld.getTransactionAmount());
        stmt.setDirection(determineDirectionByAmount(amount));
    }

    private void setSummaryInfo(FinStatement stmt, FileTransactionLabelDetail ld) {
        stmt.setDescription(ld.getTransactionSummary());
        stmt.setCounterparty(extractCounterparty(ld.getTransactionSummary()));
        stmt.setAccountRef(null);
    }

    private void inferCategoryIfNeeded(FinStatement stmt, FileTransactionLabelDetail ld, Long userId,
                                      AiTransactionCategoryService categoryService) {
        if (categoryService == null) {
            return;
        }
        
        String description = ld.getTransactionSummary() != null ? ld.getTransactionSummary() : "";
        String amountStr = ld.getTransactionAmount() != null ? ld.getTransactionAmount() : "0";
        
        inferCategory(stmt, stmt.getCounterparty(), description, amountStr, userId, categoryService);
    }

    private String extractCounterparty(String summary) {
        if (summary == null || summary.isEmpty()) {
            return null;
        }
        return summary.length() > 20 ? summary.substring(0, 20) : summary;
    }
}
