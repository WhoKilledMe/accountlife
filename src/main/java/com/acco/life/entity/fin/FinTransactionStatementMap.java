package com.acco.life.entity.fin;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 交易与账单的映射表实体类
 * 支持分期/合单/人工修正
 *
 * @author wensen.zhang
 * @version V2.0.0
 */
@Table("fin_transaction_statement_map")
@Data
public class FinTransactionStatementMap {

    /**
     * 交易-账单映射ID
     */
    @Id
    @Column("id")
    private Long id;

    /**
     * fin_transaction.id
     */
    @Column("transaction_id")
    private Long transactionId;

    /**
     * fin_statement.id
     */
    @Column("statement_id")
    private Long statementId;

    /**
     * ONE_TO_ONE/ONE_TO_MANY/MANY_TO_ONE
     */
    @Column("map_type")
    private String mapType;

    /**
     * 若是部分分配则记录该条账单映射到交易的金额
     */
    @Column("allocated_amount")
    private BigDecimal allocatedAmount;

    /**
     * AUTO/MANUAL_CONFIRMED/MANUAL_FIXED
     */
    @Column("confirm_status")
    private String confirmStatus;

    /**
     * 匹配置信度
     */
    @Column("confidence")
    private BigDecimal confidence;

    /**
     * 匹配规则标识（便于问题排查）
     */
    @Column("match_rule")
    private String matchRule;

    /**
     * 匹配详情（时间差、金额差等）JSON格式
     */
    @Column("match_detail")
    private String matchDetail;

    /**
     * 映射时间
     */
    @Column("mapped_at")
    private LocalDateTime mappedAt;
}
