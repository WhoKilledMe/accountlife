package com.acco.life.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * description: 账户交易实体类，对应数据库表account_transaction
 *
 * @date: 2025-07-24 17:54:23
 * @author wensen.zhang
 * @version V1.0.0
 */
@EqualsAndHashCode(callSuper = true)
@Table("account_transaction")
@Data
public class AccountTransaction extends BaseColumnEntity {

    /**
     * 所属用户
     */
    @Column("user_id")
    private Long userId;

    /**
     * 发生账户
     */
    @Column("account_id")
    private Long accountId;

    /**
     * 交易类型：1-income，2-expense，3-transfer_out，4-transfer_in
     */
    @Column("type")
    private Integer type;

    /**
     * 交易金额
     */
    @Column("amount")
    private BigDecimal amount;

    /**
     * 折扣金额
     */
    @Column("discount_amount")
    private BigDecimal discountAmount;

    /**
     * 分类ID
     */
    @Column("category_id")
    private Long categoryId;

    /**
     * 关联交易ID
     */
    @Column("related_transaction_id")
    private Long relatedTransactionId;

    /**
     * 摘要说明
     */
    @Column("description")
    private String description;

    /**
     * 实际发生时间
     */
    @Column("transaction_time")
    private LocalDateTime transactionTime;

    /**
     * 来源类型：1-bank，2-platform，3-credit_wallet
     */
    @Column("source_type")
    private Integer sourceType;

    /**
     * 原始账单唯一标识
     */
    @Column("source_ref")
    private String sourceRef;

    /**
     * 账单归属ID
     */
    @Column("statement_id")
    private Long statementId;
}
