package com.acco.life.service.csv;

import com.acco.life.dto.FileTransactionDto;
import com.acco.life.dto.FileTransactionAlipay;
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
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

/**
 * 支付宝账单 CSV 解析器
 *
 * @author wensen.zhang
 * @version V2.0.0
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class AlipayFinStatementParser extends AbstractFinStatementParser implements FinStatementCsvParser {

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
            if (dto instanceof FileTransactionAlipay alipay) {
                try {
                    FinStatement stmt = buildStatement(alipay, userId, fileId, categoryService);
                    statements.add(stmt);
                } catch (Exception e) {
                    log.error("解析支付宝账单行失败: {}", alipay, e);
                }
            }
        }
        
        return statements;
    }
    
    private boolean isRefundOrReturn(String productDesc, String tradeCategory, String tradeOrderNo) {
        String combined = (productDesc + " " + tradeCategory + " " + tradeOrderNo).toLowerCase();
        return combined.contains("退款") 
                || combined.contains("退货")
                || combined.contains("refund")
                || tradeOrderNo.contains("REFUND");
    }
    
    private boolean isYuebaoIncome(String productDesc, String tradeCategory) {
        String combined = (productDesc + " " + tradeCategory).toLowerCase();
        return combined.contains("余额宝") && combined.contains("收益");
    }
    
    private boolean isYuebaoTransfer(String productDesc, String tradeCategory) {
        String combined = (productDesc + " " + tradeCategory).toLowerCase();
        return combined.contains("余额宝") && (combined.contains("自动转入") || combined.contains("自动转出"));
    }

    private FinStatement buildStatement(FileTransactionAlipay alipay, Long userId, Long fileId,
                                       AiTransactionCategoryService categoryService) throws Exception {
        FinStatement stmt = new FinStatement();
        
        setBasicFields(stmt, userId, fileId);
        setOrderInfo(stmt, alipay);
        setHashAndRawData(stmt, alipay, fileId);
        setTimeAndAmount(stmt, alipay);
        setDirection(stmt, alipay);
        setMerchantInfo(stmt, alipay);
        setAccountRef(stmt, alipay);
        inferCategoryIfNeeded(stmt, alipay, userId, categoryService);
        
        return stmt;
    }

    private void setBasicFields(FinStatement stmt, Long userId, Long fileId) {
        setCommonFields(stmt, userId, fileId, getPlatformCode());
        stmt.setSourceType("PLATFORM_ORDER");
    }

    private void setOrderInfo(FinStatement stmt, FileTransactionAlipay alipay) {
        stmt.setOutTradeNo(alipay.getTradeOrderNo());
    }

    private void setHashAndRawData(FinStatement stmt, FileTransactionAlipay alipay, Long fileId) throws Exception {
        String rowHash = calculateRowHash(fileId, 
                alipay.getTradeOrderNo(), 
                alipay.getTradeTime(), 
                alipay.getAmount());
        stmt.setRawRowHash(rowHash);
        stmt.setRawData(objectMapper.writeValueAsString(alipay));
    }

    private void setTimeAndAmount(FinStatement stmt, FileTransactionAlipay alipay) {
        stmt.setStmtTime(parseDateTime(alipay.getTradeTime()));
        BigDecimal amountValue = parseAmount(alipay.getAmount());
        stmt.setAmount(amountValue.abs());
    }

    private void setDirection(FinStatement stmt, FileTransactionAlipay alipay) {
        if ("不计收支".equals(alipay.getIncomeOrExpense())) {
            String direction = determineDirectionForNonIncomeExpense(alipay);
            stmt.setDirection(direction);
        } else {
            BigDecimal amountValue = parseAmount(alipay.getAmount());
            stmt.setDirection(determineDirection(alipay.getIncomeOrExpense(), amountValue));
        }
    }
    
    private String determineDirectionForNonIncomeExpense(FileTransactionAlipay alipay) {
        String productDesc = alipay.getProductDescription() != null ? alipay.getProductDescription() : "";
        String tradeCategory = alipay.getTradeCategory() != null ? alipay.getTradeCategory() : "";
        String tradeOrderNo = alipay.getTradeOrderNo() != null ? alipay.getTradeOrderNo() : "";
        
        if (isRefundOrReturn(productDesc, tradeCategory, tradeOrderNo)) {
            return FlowDirection.IN.getCode();
        }
        
        if (isYuebaoIncome(productDesc, tradeCategory)) {
            return FlowDirection.IN.getCode();
        }
        
        return FlowDirection.OUT.getCode();
    }

    private void setMerchantInfo(FinStatement stmt, FileTransactionAlipay alipay) {
        stmt.setCounterparty(alipay.getCounterparty());
        stmt.setDescription(buildDescription(alipay));
    }

    private void setAccountRef(FinStatement stmt, FileTransactionAlipay alipay) {
        setAccountRef(stmt, alipay.getPaymentMethod(), getPlatformCode(), mappingService);
    }

    private void inferCategoryIfNeeded(FinStatement stmt, FileTransactionAlipay alipay, Long userId,
                                      AiTransactionCategoryService categoryService) {
        if (categoryService == null) {
            return;
        }
        
        String description = stmt.getDescription() != null ? stmt.getDescription() : "";
        String amountStr = formatAmountForCategoryByDirection(
                alipay.getAmount() != null ? alipay.getAmount() : "0",
                stmt.getDirection());
        
        inferCategory(stmt, stmt.getCounterparty(), description, amountStr, userId, categoryService);
    }
    
    private String formatAmountForCategoryByDirection(String amountStr, String direction) {
        String cleaned = amountStr.replaceAll("[¥￥,，]", "").trim();
        if (FlowDirection.IN.getCode().equals(direction)) {
            return cleaned;
        } else if (FlowDirection.OUT.getCode().equals(direction)) {
            return "-" + cleaned;
        }
        return cleaned;
    }

    private String buildDescription(FileTransactionAlipay alipay) {
        StringBuilder sb = new StringBuilder();
        
        appendIfNotEmpty(sb, alipay.getProductDescription(), null);
        appendIfNotEmpty(sb, alipay.getTradeCategory(), " - ");
        appendIfNotEmpty(sb, alipay.getRemark(), " (", ")");
        
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
