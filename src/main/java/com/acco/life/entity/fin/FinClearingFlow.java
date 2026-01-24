package com.acco.life.entity.fin;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 金融清算流水实体类
 * 用于表达银行/券商等清算中/已清算/失败的资金移动
 *
 * @author wensen.zhang
 * @version V2.0.0
 */
@Table("fin_clearing_flow")
@Data
public class FinClearingFlow {

    /**
     * 清算流水ID
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
     * 清算流水号
     */
    @Column("flow_no")
    private String flowNo;

    /**
     * 资金来源账户（可NULL 表示平台或外部）
     */
    @Column("from_account_id")
    private Long fromAccountId;

    /**
     * 资金去向账户（可NULL）
     */
    @Column("to_account_id")
    private Long toAccountId;

    /**
     * 关联的业务交易ID（若有关联）
     */
    @Column("transaction_id")
    private Long transactionId;

    /**
     * 本次清算金额
     */
    @Column("amount")
    private BigDecimal amount;

    /**
     * 币种
     */
    @Column("currency")
    private String currency;

    /**
     * INIT/CLEARING/SUCCESS/FAILED
     */
    @Column("status")
    private String status;

    /**
     * 清算时间或发生时间
     */
    @Column("trade_time")
    private LocalDateTime tradeTime;

    /**
     * 备注
     */
    @Column("remark")
    private String remark;

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
     * 最近修改人
     */
    @Column("updated_by")
    private String updatedBy;

    /**
     * 最近修改时间
     */
    @Column("updated_at")
    private LocalDateTime updatedAt;

    /**
     * 是否删除（0-否，1-是）
     */
    @Column("is_deleted")
    private Integer isDeleted;
}
