package com.acco.life.entity.fin;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 账户日度余额快照实体类
 * 用于报表/性能优化
 *
 * @author wensen.zhang
 * @version V2.0.0
 */
@Table("fin_account_balance_snapshot")
@Data
public class FinAccountBalanceSnapshot {

    /**
     * 快照ID
     */
    @Id
    @Column("id")
    private Long id;

    /**
     * fin_account.id
     */
    @Column("account_id")
    private Long accountId;

    /**
     * 快照日期（按日）
     */
    @Column("snapshot_date")
    private LocalDate snapshotDate;

    /**
     * 当天结束时的余额快照
     */
    @Column("balance")
    private BigDecimal balance;

    /**
     * 币种
     */
    @Column("currency")
    private String currency;

    /**
     * 记录时间
     */
    @Column("created_at")
    private LocalDateTime createdAt;
}
