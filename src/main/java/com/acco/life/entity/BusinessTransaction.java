package com.acco.life.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * description: 业务交易实体类，对应数据库表business_transaction
 *
 * @date: 2025-01-15 10:00:00
 * @author wensen.zhang
 * @version V1.0.0
 */
@EqualsAndHashCode(callSuper = true)
@Table("business_transaction")
@Data
public class BusinessTransaction extends BaseColumnEntity {

    /**
     * 交易分类ID
     */
    @Column("category_id")
    private Long categoryId;

    /**
     * 所属用户
     */
    @Column("user_id")
    private Long userId;

    /**
     * 交易金额
     */
    @Column("amount")
    private BigDecimal amount;

    /**
     * 交易发生时间
     */
    @Column("transaction_time")
    private LocalDateTime transactionTime;
}
