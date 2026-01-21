package com.acco.life.dto.fin;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 支付方式 DTO
 *
 * @author wensen.zhang
 * @version V2.0.0
 */
@Data
public class FinPaymentMethodDto {

    private Long id;
    private String paymentCode;
    private String paymentName;
    private String paymentType;
    private String platformCode;
    private String status;
    private String remark;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // 扩展字段
    private String paymentTypeName;
    private String platformName;
}
