package com.acco.life.dto.fin;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 清算流水 DTO
 *
 * @author wensen.zhang
 * @version V2.0.0
 */
@Data
public class FinClearingFlowDto {

    private Long id;
    private Long userId;
    private String flowNo;
    private Long fromAccountId;
    private Long toAccountId;
    private Long transactionId;
    private BigDecimal amount;
    private String currency;
    private String status;
    private LocalDateTime tradeTime;
    private String remark;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // 扩展字段
    private String fromAccountName;
    private String toAccountName;
    private String statusName;
}
