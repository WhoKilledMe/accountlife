package com.acco.life.service.csv;

import com.acco.life.dto.FileTransactionDto;
import com.acco.life.dto.FileTransactionJingdong;
import com.acco.life.entity.fin.FinStatement;
import com.acco.life.enums.TransactionSourceType;
import com.acco.life.enums.fin.FlowDirection;
import com.acco.life.service.AiTransactionCategoryService;
import com.acco.life.service.fin.FinStatementMappingService;
import com.acco.life.util.CsvUtil;
import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * 京东账单 CSV 解析器
 *
 * @author wensen.zhang
 * @version V2.0.0
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class JingdongFinStatementParser extends AbstractFinStatementParser implements FinStatementCsvParser {

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

            if (shouldSkip(jd)) {
                continue;
            }

            try {
                FinStatement stmt = buildStatement(jd, userId, fileId, categoryService);
                statements.add(stmt);
            } catch (Exception e) {
                log.error("解析京东账单行失败: {}", jd, e);
            }
        }

        return statements;
    }

    private boolean shouldSkip(FileTransactionJingdong jd) {
        if ("不计收支".equals(jd.getIncomeOrExpense()) && !isBaitiaoRepay(jd)) {
            log.debug("京东账单跳过不计收支记录: {}", jd);
            return true;
        }
        return false;
    }

    private FinStatement buildStatement(FileTransactionJingdong jd, Long userId, Long fileId,
                                       AiTransactionCategoryService categoryService) throws Exception {
        FinStatement stmt = new FinStatement();
        
        setBasicFields(stmt, userId, fileId);
        setOrderInfo(stmt, jd);
        setHashAndRawData(stmt, jd, fileId);
        setTimeAndAmount(stmt, jd);
        setDirection(stmt, jd);
        setMerchantInfo(stmt, jd);
        setAccountRef(stmt, jd);
        inferCategoryIfNeeded(stmt, jd, userId, categoryService);
        
        return stmt;
    }

    private void setBasicFields(FinStatement stmt, Long userId, Long fileId) {
        setCommonFields(stmt, userId, fileId, getPlatformCode());
        stmt.setSourceType("PLATFORM_ORDER");
    }

    private void setOrderInfo(FinStatement stmt, FileTransactionJingdong jd) {
        String orderNo = jd.getTradeOrderNo() != null && !jd.getTradeOrderNo().isEmpty() 
                ? jd.getTradeOrderNo() 
                : jd.getMerchantOrderNo();
        stmt.setOutTradeNo(orderNo);
    }

    private void setHashAndRawData(FinStatement stmt, FileTransactionJingdong jd, Long fileId) throws Exception {
        String rowHash = calculateRowHash(fileId, 
                jd.getTradeOrderNo(), 
                jd.getMerchantOrderNo(), 
                jd.getTradeTime(), 
                jd.getAmount());
        stmt.setRawRowHash(rowHash);
        stmt.setRawData(objectMapper.writeValueAsString(jd));
    }

    private void setTimeAndAmount(FinStatement stmt, FileTransactionJingdong jd) {
        stmt.setStmtTime(parseDateTime(jd.getTradeTime()));
        BigDecimal amountValue = parseAmount(jd.getAmount());
        stmt.setAmount(amountValue.abs());
    }

    private void setDirection(FinStatement stmt, FileTransactionJingdong jd) {
        if ("不计收支".equals(jd.getIncomeOrExpense())) {
            stmt.setDirection(FlowDirection.OUT.getCode());
        } else {
            BigDecimal amountValue = parseAmount(jd.getAmount());
            stmt.setDirection(determineDirection(jd.getIncomeOrExpense(), amountValue));
        }
    }

    private void setMerchantInfo(FinStatement stmt, FileTransactionJingdong jd) {
        stmt.setCounterparty(jd.getMerchantName());
        stmt.setDescription(buildDescription(jd));
    }

    private void setAccountRef(FinStatement stmt, FileTransactionJingdong jd) {
        setAccountRef(stmt, jd.getPaymentMethod(), getPlatformCode(), mappingService);
    }

    private void inferCategoryIfNeeded(FinStatement stmt, FileTransactionJingdong jd, Long userId,
                                      AiTransactionCategoryService categoryService) {
        if (categoryService == null) {
            return;
        }
        
        String counterparty = stmt.getCounterparty();
        String description = jd.getTradeDescription() != null ? jd.getTradeDescription() : "";
        String amountStr = formatAmountForCategory(
                jd.getAmount() != null ? jd.getAmount() : "0",
                jd.getIncomeOrExpense());
        
        inferCategory(stmt, counterparty, description, amountStr, userId, categoryService);
    }

    private boolean isBaitiaoRepay(FileTransactionJingdong jd) {
        String category = jd.getTradeCategory() != null ? jd.getTradeCategory() : "";
        String merchantName = jd.getMerchantName() != null ? jd.getMerchantName() : "";
        String desc = jd.getTradeDescription() != null ? jd.getTradeDescription() : "";
        String remark = jd.getRemark() != null ? jd.getRemark() : "";

        return category.contains("白条")
                || merchantName.contains("京东白条")
                || desc.contains("白条主动还款")
                || remark.contains("白条主动还款");
    }

    private String buildDescription(FileTransactionJingdong jd) {
        StringBuilder sb = new StringBuilder();
        
        appendIfNotEmpty(sb, jd.getTradeDescription(), null);
        appendIfNotEmpty(sb, jd.getTradeCategory(), " - ");
        appendIfNotEmpty(sb, jd.getRemark(), " (", ")");
        
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
