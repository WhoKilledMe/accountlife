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
     * 账单状态：1-open(未还)，2-paid(已还)，3-overdue(逾期)
     */
    
    @Column("status")
    private Integer status;

    /**
     * 生成时间
     */
    
    @Column("created_at")
    private LocalDateTime createdAt;
}
