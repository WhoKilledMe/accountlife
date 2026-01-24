package com.acco.life.entity.fin;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 支付拆分表实体类
 * 表达交易的支付如何由不同来源组成
 *
 * @author wensen.zhang
 * @version V2.0.0
 */
@Table("fin_payment_route")
@Data
public class FinPaymentRoute {

    /**
     * 支付路由ID
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
     * fin_payment_method.id
     */
    @Column("payment_method_id")
    private Long paymentMethodId;

    /**
     * 资金来源账户（可NULL 表示平台出资）
     */
    @Column("from_account_id")
    private Long fromAccountId;

    /**
     * 资金去向（可NULL 表示外部流出）
     */
    @Column("to_account_id")
    private Long toAccountId;

    /**
     * 拆分金额
     */
    @Column("amount")
    private BigDecimal amount;

    /**
     * 拆分顺序（展示和执行顺序）
     */
    @Column("route_order")
    private Integer routeOrder;

    /**
     * NORMAL/SUBSIDY/FEE/SPLIT
     */
    @Column("route_type")
    private String routeType;

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
