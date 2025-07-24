package com.acco.life.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Schema(name = "AccountTransactionDto", description = "AccountTransaction 数据传输对象")
public class AccountTransactionDto {

    @Schema(name = "id", description = "交易ID")
    private Integer id;

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

    @Schema(name = "createdBy", description = "创建人", accessMode = Schema.AccessMode.READ_ONLY)
    private String createdBy;

    @Schema(name = "createdAt", description = "创建时间", accessMode = Schema.AccessMode.READ_ONLY)
    private LocalDateTime createdAt;

    @Schema(name = "updatedBy", description = "修改人", accessMode = Schema.AccessMode.READ_ONLY)
    private String updatedBy;

    @Schema(name = "updatedAt", description = "修改时间", accessMode = Schema.AccessMode.READ_ONLY)
    private LocalDateTime updatedAt;

    @Schema(name = "isDeleted", description = "是否删除（0-否，1-是）", accessMode = Schema.AccessMode.READ_ONLY)
    private Integer isDeleted;

}