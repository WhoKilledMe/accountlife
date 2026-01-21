package com.acco.life.dto.fin;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 总账流水 DTO
 *
 * @author wensen.zhang
 * @version V2.0.0
 */
@Data
public class FinAccountFlowDto {

    private Long id;
    private Long userId;
    private String flowNo;
    private Long journalId;
    private Long accountId;
    private Long transactionId;
    private String direction;
    private BigDecimal amount;
    private BigDecimal balanceBefore;
    private BigDecimal balanceAfter;
    private String bizType;
    private LocalDateTime tradeTime;
    private String remark;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // 扩展字段
    private String accountName;
    private String directionName;
    private String bizTypeName;
}
