package com.acco.life.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * description: 业务交易数据传输对象，用于封装业务交易相关数据
 *
 * @date: 2025-01-15 10:00:00
 * @author wensen.zhang
 * @version V1.0.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Schema(name = "BusinessTransactionDto", description = "BusinessTransaction 数据传输对象")
public class BusinessTransactionDto extends BaseColumnDto {

    @Schema(name = "categoryId", description = "交易分类ID")
    private Long categoryId;

    @Schema(name = "categoryName", description = "分类名称（来自transaction_category.name）")
    private String categoryName;

    @Schema(name = "userId", description = "所属用户", accessMode = Schema.AccessMode.READ_ONLY)
    private Long userId;

    @Schema(name = "amount", description = "交易金额")
    private BigDecimal amount;

    @Schema(name = "transactionTime", description = "交易发生时间")
    private LocalDateTime transactionTime;
}
