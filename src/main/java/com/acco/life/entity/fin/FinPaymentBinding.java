package com.acco.life.entity.fin;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

/**
 * 支付能力与具体账户的绑定关系实体类
 * 路由表
 *
 * @author wensen.zhang
 * @version V2.0.0
 */
@Table("fin_payment_binding")
@Data
public class FinPaymentBinding {

    /**
     * 绑定ID
     */
    @Id
    @Column("id")
    private Long id;

    /**
     * fin_payment_method.id
     */
    @Column("payment_method_id")
    private Long paymentMethodId;

    /**
     * fin_account.id
     */
    @Column("account_id")
    private Long accountId;

    /**
     * 路由优先级，数值越大优先
     */
    @Column("priority")
    private Integer priority;

    /**
     * 是否启用
     */
    @Column("enabled")
    private Boolean enabled;

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
