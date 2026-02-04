package com.acco.life.service.csv;

import com.acco.life.entity.fin.FinStatement;
import com.acco.life.enums.fin.FlowDirection;
import com.acco.life.enums.fin.StatementStatus;
import com.acco.life.service.AiTransactionCategoryService;
import com.acco.life.service.fin.FinStatementMappingService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * 账单解析器抽象基类
 * 提供公共的工具方法
 *
 * @author wensen.zhang
 * @version V2.0.0
 */
@Slf4j
public abstract class AbstractFinStatementParser {

    protected final ObjectMapper objectMapper = new ObjectMapper();
    protected final CsvMapper csvMapper = new CsvMapper();

    protected LocalDateTime parseDateTime(String dateTimeStr) {
        if (dateTimeStr == null || dateTimeStr.isEmpty()) {
            return LocalDateTime.now();
        }
        try {
            String normalized = dateTimeStr.replace("/", "-").trim();
            if (normalized.length() == 10) {
                normalized += " 00:00:00";
            }
            return LocalDateTime.parse(normalized, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        } catch (Exception e) {
            log.warn("解析时间失败: {}", dateTimeStr);
            return LocalDateTime.now();
        }
    }

    protected BigDecimal parseAmount(String amountStr) {
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

    protected String calculateRowHash(Long fileId, String... parts) {
        StringBuilder rawContent = new StringBuilder();
        for (String part : parts) {
            rawContent.append(part != null ? part : "");
        }
        return org.springframework.util.DigestUtils.md5DigestAsHex(
                (fileId + rawContent.toString()).getBytes(StandardCharsets.UTF_8));
    }

    protected void setCommonFields(FinStatement stmt, Long userId, Long fileId, String platformCode) {
        stmt.setFileId(fileId);
        stmt.setUserId(userId);
        stmt.setPlatformCode(platformCode);
        stmt.setParserVersion(getParserVersion());
        stmt.setParsedAt(LocalDateTime.now());
        stmt.setRetryCount(0);
        stmt.setStatus(StatementStatus.PARSED.getCode());
        stmt.setCreatedAt(LocalDateTime.now());
    }

    protected void inferCategory(FinStatement stmt, String counterparty, String description, 
                                 String amountStr, Long userId, AiTransactionCategoryService categoryService) {
        if (categoryService == null) {
            return;
        }
        try {
            String cleanAmount = amountStr.replaceAll("[¥￥,，]", "").trim();
            var category = categoryService.inferTransactionCategory(counterparty, description, cleanAmount, userId).block();
            if (category != null && category.getId() != null) {
                stmt.setCategoryId(category.getId());
                log.debug("账单分类推断成功: {} [{}] -> {}", counterparty, description, category.getName());
            }
        } catch (Exception e) {
            log.warn("账单分类推断失败: {} - {}", counterparty, description, e);
        }
    }

    protected String determineDirection(String incomeOrExpense, BigDecimal amount) {
        if ("收入".equals(incomeOrExpense)) {
            return FlowDirection.IN.getCode();
        } else if ("支出".equals(incomeOrExpense)) {
            return FlowDirection.OUT.getCode();
        } else {
            return amount.compareTo(BigDecimal.ZERO) >= 0 
                    ? FlowDirection.OUT.getCode() 
                    : FlowDirection.IN.getCode();
        }
    }

    protected String determineDirectionByAmount(BigDecimal amount) {
        return amount.compareTo(BigDecimal.ZERO) >= 0 
                ? FlowDirection.OUT.getCode() 
                : FlowDirection.IN.getCode();
    }

    protected String extractAccountRefFromPaymentMethod(String paymentMethod, String platformCode, 
                                                        FinStatementMappingService mappingService) {
        if (paymentMethod == null || paymentMethod.isEmpty()) {
            return null;
        }
        String pm = paymentMethod.trim();

        if (mappingService != null) {
            try {
                String mapped = mappingService.mapAccountRef(platformCode, pm).block();
                if (mapped != null && !mapped.isEmpty() && !pm.equals(mapped)) {
                    return mapped;
                }
            } catch (Exception e) {
                log.warn("账单 account_ref 映射规则执行失败，回退到本地逻辑: {}", pm, e);
            }
        }

        int start = pm.indexOf('(');
        int end = pm.indexOf(')');
        if (start > 0 && end > start) {
            String tail = pm.substring(start + 1, end).trim();
            if (!tail.isEmpty()) {
                return tail;
            }
        }

        return pm;
    }

    protected String formatAmountForCategory(String amountStr, String incomeOrExpense) {
        String cleaned = amountStr.replaceAll("[¥￥,，]", "").trim();
        if ("支出".equals(incomeOrExpense)) {
            return "-" + cleaned;
        }
        return cleaned;
    }

    public abstract String getParserVersion();
}
