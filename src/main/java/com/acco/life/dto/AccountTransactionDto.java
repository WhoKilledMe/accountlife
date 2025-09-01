package com.acco.life.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * description: 账户交易数据传输对象，用于封装账户交易相关数据
 *
 * @date: 2025-07-24 17:54:23
 * @author wensen.zhang
 * @version V1.0.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Schema(name = "AccountTransactionDto", description = "AccountTransaction 数据传输对象")
public class AccountTransactionDto extends BaseColumnDto {

    @Schema(name = "userId", description = "所属用户", accessMode = Schema.AccessMode.READ_ONLY)
    private Integer userId;

    @Schema(name = "accountId", description = "发生账户")
    private Integer accountId;

    @Schema(name = "type", description = "交易类型：1-income，2-expense，3-transfer_out，4-transfer_in")
    private Integer type;

    @Schema(name = "amount", description = "交易金额")
    private BigDecimal amount;

    @Schema(name = "categoryId", description = "分类ID")
    private Integer categoryId;

    @Schema(name = "relatedTransactionId", description = "关联交易ID")
    private Integer relatedTransactionId;

    @Schema(name = "description", description = "摘要说明")
    private String description;

    @Schema(name = "transactionTime", description = "实际发生时间")
    private LocalDateTime transactionTime;

    @Schema(name = "sourceType", description = "来源类型：1-bank，2-platform，3-credit_wallet")
    private Integer sourceType;

    @Schema(name = "sourceRef", description = "原始账单唯一标识")
    private String sourceRef;

    @Schema(name = "statementId", description = "账单归属ID")
    private Integer statementId;
}