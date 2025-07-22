package com.acco.life.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Table("credit_wallet_statement")
@Data
public class CreditWalletStatement {


    /**
     * 账单ID
     */
    @Id
    @Column("id")
    private Integer id;

    /**
     * 信用钱包账户ID
     */
    
    @Column("account_id")
    private Integer accountId;

    /**
     * 所属用户
     */
    
    @Column("user_id")
    private Integer userId;

    /**
     * 账单期开始日期
     */
    
    @Column("billing_period_start")
    private LocalDateTime billingPeriodStart;

    /**
     * 账单期结束日期
     */
    
    @Column("billing_period_end")
    private LocalDateTime billingPeriodEnd;

    /**
     * 应还金额
     */
    
    @Column("total_amount")
    private BigDecimal totalAmount;

    /**
     * 还款到期日
     */
    
    @Column("repay_due_date")
    private LocalDateTime repayDueDate;

    /**
     * 实际还款日
     */
    
    @Column("repay_date")
    private LocalDateTime repayDate;

    /**
     * 账单状态：1-open，2-paid，3-overdue
     */
    
    @Column("status")
    private Integer status;

    /**
     * 创建人
     */
    
    @Column("created_by")
    private String createdBy;

    /**
     * 生成时间
     */
    
    @Column("created_at")
    private LocalDateTime createdAt;

    /**
     * 修改人
     */
    
    @Column("updated_by")
    private String updatedBy;

    /**
     * 修改时间
     */
    
    @Column("updated_at")
    private LocalDateTime updatedAt;

    /**
     * 是否删除（0-否，1-是）
     */
    
    @Column("is_deleted")
    private Integer isDeleted;
}
