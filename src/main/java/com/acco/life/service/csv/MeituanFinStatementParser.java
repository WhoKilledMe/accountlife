package com.acco.life.service.csv;

import com.acco.life.dto.FileTransactionDto;
import com.acco.life.dto.FileTransactionMeituan;
import com.acco.life.entity.fin.FinStatement;
import com.acco.life.enums.TransactionSourceType;
import com.acco.life.enums.fin.FlowDirection;
import com.acco.life.enums.fin.StatementStatus;
import com.acco.life.util.CsvUtil;
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
 * 美团账单 CSV 解析器（新版，返回 FinStatement）
 *
 * @author wensen.zhang
 * @version V2.0.0
 */
@Slf4j
@Component
public class MeituanFinStatementParser implements FinStatementCsvParser {

    private final CsvMapper csvMapper = new CsvMapper();
    private final ObjectMapper objectMapper = new ObjectMapper();

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
        // 美团账单前19行为元数据，需要跳过
        // 第20行为表头，第21行开始是数据
        InputStream skippedStream = CsvUtil.skipLines(inputStream, 19);
        
        CsvSchema schema = csvMapper.schemaFor(FileTransactionMeituan.class).withHeader();
        MappingIterator<FileTransactionMeituan> it = csvMapper.readerFor(FileTransactionMeituan.class)
                .with(schema)
                .readValues(skippedStream);
        return it.readAll();
    }

    @Override
    public List<FinStatement> buildStatements(List<? extends FileTransactionDto> dtos, Long userId, Long fileId,
                                              com.acco.life.service.AiTransactionCategoryService categoryService) {
        List<FinStatement> statements = new ArrayList<>();
        
        for (FileTransactionDto dto : dtos) {
            if (!(dto instanceof FileTransactionMeituan mt)) {
                continue;
            }
            
            try {
                FinStatement stmt = new FinStatement();
                stmt.setFileId(fileId);
                stmt.setUserId(userId);
                stmt.setPlatformCode(getPlatformCode());
                stmt.setSourceType("PLATFORM_ORDER");
                
                // 设置订单号 - 使用交易单号
                stmt.setOutTradeNo(mt.getTradeNo());
                
                // 计算行级 Hash
                String tradeNo = mt.getTradeNo() != null ? mt.getTradeNo() : "";
                String successTime = mt.getSuccessTime() != null ? mt.getSuccessTime() : "";
                String paidAmount = mt.getPaidAmount() != null ? mt.getPaidAmount() : "";
                String rawContent = tradeNo + successTime + paidAmount;
                String rowHash = DigestUtils.md5DigestAsHex((fileId + rawContent).getBytes(StandardCharsets.UTF_8));
                stmt.setRawRowHash(rowHash);
                
                // 存储原始 JSON
                stmt.setRawData(objectMapper.writeValueAsString(mt));
                
                // 解析时间 - 优先使用成功时间，如果没有则使用创建时间
                String timeStr = mt.getSuccessTime() != null ? mt.getSuccessTime() : mt.getCreatedTime();
                stmt.setStmtTime(parseDateTime(timeStr));
                
                // 解析金额 - 使用实付金额
                BigDecimal amount = parseAmount(mt.getPaidAmount());
                stmt.setAmount(amount.abs());
                stmt.setDirection(FlowDirection.OUT.getCode()); // 美团消费都是支出
                
                // 设置商户信息 - 从订单标题或备注中提取
                stmt.setCounterparty(extractMerchantName(mt));
                stmt.setDescription(buildDescription(mt));
                
                // AI分类推断（结合counterparty和description）
                if (categoryService != null) {
                    try {
                        String counterparty = stmt.getCounterparty();  // 商户名称
                        String description = stmt.getDescription() != null ? stmt.getDescription() : "";
                        String amountStr = mt.getPaidAmount() != null ? mt.getPaidAmount() : "0";
                        // 美团都是支出，金额取负值
                        amountStr = "-" + amountStr.replaceAll("[¥￥,，]", "").trim();
                        
                        // 使用新方法：传入counterparty + description
                        var category = categoryService.inferTransactionCategory(counterparty, description, amountStr, userId).block();
                        if (category != null && category.getId() != null) {
                            stmt.setCategoryId(category.getId());
                            log.debug("美团账单分类推断成功: {} [{}] -> {}", counterparty, description, category.getName());
                        }
                    } catch (Exception e) {
                        log.warn("美团账单分类推断失败: {} - {}", stmt.getCounterparty(), stmt.getDescription(), e);
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
                log.error("解析美团账单行失败: {}", mt, e);
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

    private String extractMerchantName(FileTransactionMeituan mt) {
        // 从订单标题中提取商户名，如果没有则返回null
        if (mt.getOrderTitle() != null && !mt.getOrderTitle().isEmpty()) {
            return mt.getOrderTitle();
        }
        return null;
    }

    private String buildDescription(FileTransactionMeituan mt) {
        StringBuilder sb = new StringBuilder();
        if (mt.getOrderTitle() != null) {
            sb.append(mt.getOrderTitle());
        }
        if (mt.getTransactionType() != null) {
            sb.append(" - ").append(mt.getTransactionType());
        }
        if (mt.getRemark() != null && !mt.getRemark().isEmpty()) {
            sb.append(" (").append(mt.getRemark()).append(")");
        }
        return sb.toString();
    }
}
