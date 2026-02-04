package com.acco.life.service.csv;

import com.acco.life.dto.FileTransactionDto;
import com.acco.life.dto.FileTransactionWechat;
import com.acco.life.entity.fin.FinStatement;
import com.acco.life.enums.TransactionSourceType;
import com.acco.life.enums.fin.FlowDirection;
import com.acco.life.enums.fin.StatementStatus;
import com.acco.life.service.AiTransactionCategoryService;
import com.acco.life.service.fin.FinStatementMappingService;
import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import lombok.RequiredArgsConstructor;
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
 * 微信账单 CSV 解析器（新版，返回 FinStatement）
 *
 * @author wensen.zhang
 * @version V2.0.0
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class WechatFinStatementParser implements FinStatementCsvParser {

    private final CsvMapper csvMapper = new CsvMapper();
    private final ObjectMapper objectMapper = new ObjectMapper();
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
        // 微信账单通常前几行是元数据，需要跳过
        // 这里假设第一行是表头，如果实际格式不同需要调整
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
            
            try {
                // 只处理支付成功的记录
                if (!"支付成功".equals(wechat.getCurrentStatus())) {
                    log.debug("跳过非成功交易: {}", wechat.getTradeNo());
                    continue;
                }
                
                FinStatement stmt = new FinStatement();
                stmt.setFileId(fileId);
                stmt.setUserId(userId);
                stmt.setPlatformCode(getPlatformCode());
                stmt.setSourceType("PLATFORM_ORDER");
                
                // 设置订单号 - 优先使用交易单号，如果没有则使用商户单号
                String orderNo = wechat.getTradeNo() != null && !wechat.getTradeNo().isEmpty() 
                        ? wechat.getTradeNo() 
                        : wechat.getMerchantNo();
                stmt.setOutTradeNo(orderNo);
                
                // 计算行级 Hash
                String tradeNo = wechat.getTradeNo() != null ? wechat.getTradeNo() : "";
                String merchantNo = wechat.getMerchantNo() != null ? wechat.getMerchantNo() : "";
                String tradeTime = wechat.getTradeTime() != null ? wechat.getTradeTime() : "";
                String amount = wechat.getAmount() != null ? wechat.getAmount() : "";
                String rawContent = tradeNo + merchantNo + tradeTime + amount;
                String rowHash = DigestUtils.md5DigestAsHex((fileId + rawContent).getBytes(StandardCharsets.UTF_8));
                stmt.setRawRowHash(rowHash);
                
                // 存储原始 JSON
                stmt.setRawData(objectMapper.writeValueAsString(wechat));
                
                // 解析时间
                stmt.setStmtTime(parseDateTime(wechat.getTradeTime()));
                
                // 解析金额和方向
                BigDecimal amountValue = parseAmount(wechat.getAmount());
                stmt.setAmount(amountValue.abs());
                
                // 根据收/支字段判断方向
                String direction;
                if ("收入".equals(wechat.getIncomeOrExpense())) {
                    direction = FlowDirection.IN.getCode();
                } else if ("支出".equals(wechat.getIncomeOrExpense())) {
                    direction = FlowDirection.OUT.getCode();
                } else {
                    // 默认支出
                    direction = FlowDirection.OUT.getCode();
                }
                stmt.setDirection(direction);
                
                // 设置商户信息
                stmt.setCounterparty(wechat.getCounterparty());
                stmt.setDescription(buildDescription(wechat));
                
                // 设置账户引用 - 先通过配置化映射，再从支付方式中提取（如"宁波银行信用卡(4573)"）
                stmt.setAccountRef(extractAccountRef(wechat.getPaymentMethod()));
                
                // AI分类推断
                if (categoryService != null) {
                    try {
                        String description = stmt.getDescription() != null ? stmt.getDescription() : "";
                        String amountStr = wechat.getAmount() != null ? wechat.getAmount() : "0";
                        // 根据方向调整金额符号
                        if ("支出".equals(wechat.getIncomeOrExpense())) {
                            amountStr = "-" + amountStr.replaceAll("[¥￥,，]", "").trim();
                        } else {
                            amountStr = amountStr.replaceAll("[¥￥,，]", "").trim();
                        }
                        
                        // 使用新方法：传入counterparty + description
                        var category = categoryService.inferTransactionCategory(stmt.getCounterparty(), description, amountStr, userId).block();
                        if (category != null && category.getId() != null) {
                            stmt.setCategoryId(category.getId());
                            log.debug("微信账单分类推断成功: {} [{}] -> {}", stmt.getCounterparty(), description, category.getName());
                        }
                    } catch (Exception e) {
                        log.warn("微信账单分类推断失败: {} - {}", stmt.getCounterparty(), stmt.getDescription(), e);
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
                log.error("解析微信账单行失败: {}", wechat, e);
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

    private String buildDescription(FileTransactionWechat wechat) {
        StringBuilder sb = new StringBuilder();
        if (wechat.getProduct() != null && !wechat.getProduct().isEmpty()) {
            sb.append(wechat.getProduct());
        }
        if (wechat.getTradeType() != null && !wechat.getTradeType().isEmpty()) {
            if (sb.length() > 0) {
                sb.append(" - ");
            }
            sb.append(wechat.getTradeType());
        }
        if (wechat.getRemark() != null && !wechat.getRemark().isEmpty()) {
            if (sb.length() > 0) {
                sb.append(" (");
            }
            sb.append(wechat.getRemark());
            if (sb.length() > 0 && sb.charAt(sb.length() - 1) != ')') {
                sb.append(")");
            }
        }
        return sb.toString();
    }

    /**
     * 从收/付款方式中提取账户引用，先走配置表规则，再做通用兜底逻辑
     */
    private String extractAccountRef(String paymentMethod) {
        if (paymentMethod == null || paymentMethod.isEmpty()) {
            return null;
        }
        String pm = paymentMethod.trim();

        // 优先使用配置化映射规则
        try {
            String mapped = mappingService.mapAccountRef(getPlatformCode(), pm).block();
            if (mapped != null && !mapped.isEmpty() && !pm.equals(mapped)) {
                return mapped;
            }
        } catch (Exception e) {
            log.warn("微信账单 account_ref 映射规则执行失败，回退到本地逻辑: {}", pm, e);
        }

        // 兜底逻辑：兼容未配置规则的情况
        // 尝试提取卡号尾号，如"宁波银行信用卡(4573)" -> "4573"
        int start = pm.indexOf('(');
        int end = pm.indexOf(')');
        if (start > 0 && end > start) {
            String tail = pm.substring(start + 1, end).trim();
            if (!tail.isEmpty()) {
                return tail;
            }
        }

        // 其他情况：直接返回原始支付方式文本
        return pm;
    }
}
