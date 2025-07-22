package com.acco.life.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Table("account_transaction")
@Data
public class AccountTransaction {


    /**
     * 交易ID
     */
    @Id
    @Column("id")
    private Integer id;

    /**
     * 所属用户
     */
    
    @Column("user_id")
    private Integer userId;

    /**
     * 发生账户
     */
    
    @Column("account_id")
    private Integer accountId;

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
     * 分类ID
     */
    
    @Column("category_id")
    private Integer categoryId;

    /**
     * 关联交易ID
     */
    
    @Column("related_transaction_id")
    private Integer relatedTransactionId;

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
    private Integer statementId;

    /**
     * 创建人
     */
    
    @Column("created_by")
    private String createdBy;

    /**
     * 创建时间
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
