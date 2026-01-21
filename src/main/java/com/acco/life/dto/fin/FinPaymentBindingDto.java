package com.acco.life.dto.fin;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 支付方式绑定 DTO
 *
 * @author wensen.zhang
 * @version V2.0.0
 */
@Data
public class FinPaymentBindingDto {

    private Long id;
    private Long paymentMethodId;
    private Long accountId;
    private Integer priority;
    private Boolean enabled;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // 扩展字段
    private String paymentMethodName;
    private String accountName;
}
