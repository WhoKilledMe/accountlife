package com.acco.life.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Schema(name = "CreditWalletStatementDto", description = "CreditWalletStatement 数据传输对象")
public class CreditWalletStatementDto {

    @Schema(name = "id", description = "账单ID", accessMode = Schema.AccessMode.READ_ONLY)
    private Integer id;

    @Schema(name = "accountId", description = "信用钱包账户ID")
    private Integer accountId;

    @Schema(name = "userId", description = "所属用户", accessMode = Schema.AccessMode.READ_ONLY)
    private Integer userId;

    @Schema(name = "billingPeriodStart", description = "账单期开始日期")
    private LocalDateTime billingPeriodStart;

    @Schema(name = "billingPeriodEnd", description = "账单期结束日期")
    private LocalDateTime billingPeriodEnd;

    @Schema(name = "totalAmount", description = "应还金额")
    private BigDecimal totalAmount;

    @Schema(name = "repayDueDate", description = "还款到期日")
    private LocalDateTime repayDueDate;

    @Schema(name = "repayDate", description = "实际还款日")
    private LocalDateTime repayDate;

    @Schema(name = "status", description = "账单状态：1-open，2-paid，3-overdue")
    private Integer status;

    @Schema(name = "createdBy", description = "创建人", accessMode = Schema.AccessMode.READ_ONLY)
    private String createdBy;

    @Schema(name = "createdAt", description = "生成时间", accessMode = Schema.AccessMode.READ_ONLY)
    private LocalDateTime createdAt;

    @Schema(name = "updatedBy", description = "修改人", accessMode = Schema.AccessMode.READ_ONLY)
    private String updatedBy;

    @Schema(name = "updatedAt", description = "修改时间", accessMode = Schema.AccessMode.READ_ONLY)
    private LocalDateTime updatedAt;

    @Schema(name = "isDeleted", description = "是否删除（0-否，1-是）", accessMode = Schema.AccessMode.READ_ONLY)
    private Integer isDeleted;

}