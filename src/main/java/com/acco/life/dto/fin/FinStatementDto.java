package com.acco.life.dto.fin;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 账单行 DTO
 *
 * @author wensen.zhang
 * @version V2.0.0
 */
@Data
public class FinStatementDto {

    private Long id;
    private Long fileId;
    private Long userId;
    private String platformCode;
    private String sourceType;
    private String rawRowHash;
    private String rawData;
    private String outTradeNo;
    private LocalDateTime stmtTime;
    private BigDecimal amount;
    private String direction;
    private String counterparty;
    private String accountRef;
    private String description;
    private Long categoryId;
    private String parserVersion;
    private Integer retryCount;
    private LocalDateTime parsedAt;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // 扩展字段
    private String platformName;
    private String statusName;
    private String directionName;
    
    // 映射的账户信息
    private Long mappedAccountId;
    private String mappedAccountName;
    
    // 映射的交易信息
    private Long mappedTransactionId;
}
