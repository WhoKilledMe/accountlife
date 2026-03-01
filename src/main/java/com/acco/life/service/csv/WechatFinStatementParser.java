package com.acco.life.service.csv;

import com.acco.life.dto.FileTransactionDto;
import com.acco.life.dto.FileTransactionWechat;
import com.acco.life.entity.fin.FinStatement;
import com.acco.life.enums.TransactionSourceType;
import com.acco.life.service.AiTransactionCategoryService;
import com.acco.life.service.fin.FinStatementMappingService;
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
 * 微信账单 CSV 解析器
 *
 * @author wensen.zhang
 * @version V2.0.0
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class WechatFinStatementParser extends AbstractFinStatementParser implements FinStatementCsvParser {

    private final FinStatementMappingService mappingService;

    @Override
    public boolean supports(TransactionSourceType sourceType) {
        return sourceType == TransactionSourceType.WECHAT;
    }

    @Override
    public String getPlatformCode() {
        return "WECHAT";
    }

    @Override
    public String getParserVersion() {
        return "2.0.0";
    }

    @Override
    public List<? extends FileTransactionDto> parse(InputStream inputStream) throws Exception {
        CsvSchema schema = csvMapper.schemaFor(FileTransactionWechat.class).withHeader();
        MappingIterator<FileTransactionWechat> it = csvMapper.readerFor(FileTransactionWechat.class)
                .with(schema)
                .readValues(inputStream);
        return it.readAll();
    }

    @Override
    public List<FinStatement> buildStatements(List<? extends FileTransactionDto> dtos, Long userId, Long fileId,
                                              AiTransactionCategoryService categoryService) {
        List<FinStatement> statements = new ArrayList<>();
        
        for (FileTransactionDto dto : dtos) {
            if (!(dto instanceof FileTransactionWechat wechat)) {
                continue;
            }
            
            if (shouldSkip(wechat)) {
                continue;
            }
            
            try {
                FinStatement stmt = buildStatement(wechat, userId, fileId, categoryService);
                statements.add(stmt);
            } catch (Exception e) {
                log.error("解析微信账单行失败: {}", wechat, e);
            }
        }
        
        return statements;
    }

    private boolean shouldSkip(FileTransactionWechat wechat) {
        if (!"支付成功".equals(wechat.getCurrentStatus())) {
            log.debug("跳过非成功交易: {}", wechat.getTradeNo());
            return true;
        }
        return false;
    }

    private FinStatement buildStatement(FileTransactionWechat wechat, Long userId, Long fileId,
                                       AiTransactionCategoryService categoryService) throws Exception {
        FinStatement stmt = new FinStatement();
        
        setBasicFields(stmt, userId, fileId);
        setOrderInfo(stmt, wechat);
        setHashAndRawData(stmt, wechat, fileId);
        setTimeAndAmount(stmt, wechat);
        setDirection(stmt, wechat);
        setMerchantInfo(stmt, wechat);
        setAccountRef(stmt, wechat);
        inferCategoryIfNeeded(stmt, wechat, userId, categoryService);
        
        return stmt;
    }

    private void setBasicFields(FinStatement stmt, Long userId, Long fileId) {
        setCommonFields(stmt, userId, fileId, getPlatformCode());
        stmt.setSourceType("PLATFORM_ORDER");
    }

    private void setOrderInfo(FinStatement stmt, FileTransactionWechat wechat) {
        String orderNo = wechat.getTradeNo() != null && !wechat.getTradeNo().isEmpty() 
                ? wechat.getTradeNo() 
                : wechat.getMerchantNo();
        stmt.setOutTradeNo(orderNo);
    }

    private void setHashAndRawData(FinStatement stmt, FileTransactionWechat wechat, Long fileId) throws Exception {
        String rowHash = calculateRowHash(fileId, 
                wechat.getTradeNo(), 
                wechat.getMerchantNo(), 
                wechat.getTradeTime(), 
                wechat.getAmount());
        stmt.setRawRowHash(rowHash);
        stmt.setRawData(objectMapper.writeValueAsString(wechat));
    }

    private void setTimeAndAmount(FinStatement stmt, FileTransactionWechat wechat) {
        stmt.setStmtTime(parseDateTime(wechat.getTradeTime()));
        BigDecimal amountValue = parseAmount(wechat.getAmount());
        stmt.setAmount(amountValue.abs());
    }

    private void setDirection(FinStatement stmt, FileTransactionWechat wechat) {
        BigDecimal amountValue = parseAmount(wechat.getAmount());
        stmt.setDirection(determineDirection(wechat.getIncomeOrExpense(), amountValue));
    }

    private void setMerchantInfo(FinStatement stmt, FileTransactionWechat wechat) {
        stmt.setCounterparty(wechat.getCounterparty());
        stmt.setDescription(buildDescription(wechat));
    }

    private void setAccountRef(FinStatement stmt, FileTransactionWechat wechat) {
        setAccountRef(stmt, wechat.getPaymentMethod(), getPlatformCode(), mappingService);
    }

    private void inferCategoryIfNeeded(FinStatement stmt, FileTransactionWechat wechat, Long userId,
                                      AiTransactionCategoryService categoryService) {
        if (categoryService == null) {
            return;
        }
        
        String description = stmt.getDescription() != null ? stmt.getDescription() : "";
        String amountStr = formatAmountForCategory(
                wechat.getAmount() != null ? wechat.getAmount() : "0",
                wechat.getIncomeOrExpense());
        
        inferCategory(stmt, stmt.getCounterparty(), description, amountStr, userId, categoryService);
    }

    private String buildDescription(FileTransactionWechat wechat) {
        StringBuilder sb = new StringBuilder();
        
        appendIfNotEmpty(sb, wechat.getProduct(), null);
        appendIfNotEmpty(sb, wechat.getTradeType(), " - ");
        appendIfNotEmpty(sb, wechat.getRemark(), " (", ")");
        
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
