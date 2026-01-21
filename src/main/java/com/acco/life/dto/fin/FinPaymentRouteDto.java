package com.acco.life.dto.fin;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 支付拆分 DTO
 *
 * @author wensen.zhang
 * @version V2.0.0
 */
@Data
public class FinPaymentRouteDto {

    private Long id;
    private Long transactionId;
    private Long paymentMethodId;
    private Long fromAccountId;
    private Long toAccountId;
    private BigDecimal amount;
    private Integer routeOrder;
    private String routeType;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // 扩展字段
    private String paymentMethodName;
    private String fromAccountName;
    private String toAccountName;
    private String routeTypeName;
}
