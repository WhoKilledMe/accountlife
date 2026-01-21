package com.acco.life.entity.fin;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 总账流水（会计明细）实体类
 * 所有账户余额变动应通过该表记录（复式记账的边）
 *
 * @author wensen.zhang
 * @version V2.0.0
 */
@Table("fin_account_flow")
@Data
public class FinAccountFlow {

    /**
     * 会计流水ID
     */
    @Id
    @Column("id")
    private Long id;

    /**
     * 所属用户
     */
    @Column("user_id")
    private Long userId;

    /**
     * 会计流水号（全局唯一）
     */
    @Column("flow_no")
    private String flowNo;

    /**
     * 会计分录组ID（同一笔业务的借贷方共享，用于验证借贷平衡）
     */
    @Column("journal_id")
    private Long journalId;

    /**
     * 变动账户ID（fin_account.id）
     */
    @Column("account_id")
    private Long accountId;

    /**
     * 关联业务交易（fin_transaction.id）
     */
    @Column("transaction_id")
    private Long transactionId;

    /**
     * DEBIT/CREDIT（借/贷方向，便于复式记账）
     */
    @Column("direction")
    private String direction;

    /**
     * 变动金额（正数）
     */
    @Column("amount")
    private BigDecimal amount;

    /**
     * 余额变更前（快照）
     */
    @Column("balance_before")
    private BigDecimal balanceBefore;

    /**
     * 余额变更后（快照）
     */
    @Column("balance_after")
    private BigDecimal balanceAfter;

    /**
     * 业务类型，复制自 fin_transaction.biz_type 便于查询
     */
    @Column("biz_type")
    private String bizType;

    /**
     * 发生时间（用于排序/回溯）
     */
    @Column("trade_time")
    private LocalDateTime tradeTime;

    /**
     * 备注
     */
    @Column("remark")
    private String remark;

    /**
     * 创建时间
     */
    @Column("created_at")
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    @Column("updated_at")
    private LocalDateTime updatedAt;
}
