package com.acco.life.service.csv;

import com.acco.life.dto.FileTransactionDto;
import com.acco.life.dto.FileTransactionNingBoBank;
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
 * 宁波银行信用卡账单 CSV 解析器
 *
 * @author wensen.zhang
 * @version V2.0.0
 */
@Slf4j
@Component
public class NingboFinStatementParser extends AbstractFinStatementParser implements FinStatementCsvParser {

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
                FinStatement stmt = buildStatement(nb, userId, fileId, categoryService);
                statements.add(stmt);
            } catch (Exception e) {
                log.error("解析宁波银行账单行失败: {}", nb, e);
            }
        }
        
        return statements;
    }

    private FinStatement buildStatement(FileTransactionNingBoBank nb, Long userId, Long fileId,
                                       AiTransactionCategoryService categoryService) throws Exception {
        FinStatement stmt = new FinStatement();
        
        setBasicFields(stmt, userId, fileId);
        setHashAndRawData(stmt, nb, fileId);
        setTimeAndAmount(stmt, nb);
        setDirection(stmt, nb);
        setSummaryInfo(stmt, nb);
        inferCategoryIfNeeded(stmt, nb, userId, categoryService);
        
        return stmt;
    }

    private void setBasicFields(FinStatement stmt, Long userId, Long fileId) {
        setCommonFields(stmt, userId, fileId, getPlatformCode());
        stmt.setSourceType("CREDIT_CARD_STATEMENT");
    }

    private void setHashAndRawData(FinStatement stmt, FileTransactionNingBoBank nb, Long fileId) throws Exception {
        String rowHash = calculateRowHash(fileId, 
                nb.getTransactionDate(), 
                nb.getTransactionAmount(), 
                nb.getTransactionSummary());
        stmt.setRawRowHash(rowHash);
        stmt.setRawData(objectMapper.writeValueAsString(nb));
    }

    private void setTimeAndAmount(FinStatement stmt, FileTransactionNingBoBank nb) {
        String dateStr = nb.getTransactionDate() != null ? nb.getTransactionDate() : nb.getAccountingDate();
        stmt.setStmtTime(parseDateTime(dateStr));
        BigDecimal amount = parseAmount(nb.getTransactionAmount());
        stmt.setAmount(amount.abs());
    }

    private void setDirection(FinStatement stmt, FileTransactionNingBoBank nb) {
        BigDecimal amount = parseAmount(nb.getTransactionAmount());
        stmt.setDirection(determineDirectionByAmount(amount));
    }

    private void setSummaryInfo(FinStatement stmt, FileTransactionNingBoBank nb) {
        stmt.setDescription(nb.getTransactionSummary());
        stmt.setCounterparty(extractCounterparty(nb.getTransactionSummary()));
        stmt.setAccountRef(null);
    }

    private void inferCategoryIfNeeded(FinStatement stmt, FileTransactionNingBoBank nb, Long userId,
                                      AiTransactionCategoryService categoryService) {
        if (categoryService == null) {
            return;
        }
        
        String description = nb.getTransactionSummary() != null ? nb.getTransactionSummary() : "";
        String amountStr = nb.getTransactionAmount() != null ? nb.getTransactionAmount() : "0";
        
        inferCategory(stmt, stmt.getCounterparty(), description, amountStr, userId, categoryService);
    }

    private String extractCounterparty(String summary) {
        if (summary == null || summary.isEmpty()) {
            return null;
        }
        return summary.length() > 20 ? summary.substring(0, 20) : summary;
    }
}
