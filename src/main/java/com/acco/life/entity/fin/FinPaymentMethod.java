package com.acco.life.entity.fin;

import com.acco.life.entity.BaseColumnEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

/**
 * 支付方式能力表实体类
 * 抽象支付通道/能力
 *
 * @author wensen.zhang
 * @version V2.0.0
 */
@EqualsAndHashCode(callSuper = true)
@Table("fin_payment_method")
@Data
public class FinPaymentMethod extends BaseColumnEntity {

    /**
     * 支付方式代码（WECHAT_PAY/ALIPAY_PAY/BANK_DEBIT/CREDIT_PAY/MEITUAN_MONTH）
     */
    @Column("payment_code")
    private String paymentCode;

    /**
     * 人类可读名称，如：微信支付（聚合）
     */
    @Column("payment_name")
    private String paymentName;

    /**
     * 支付类别：PLATFORM/BALANCE/CREDIT/BANK/MIXED/SUBSIDY
     */
    @Column("payment_type")
    private String paymentType;

    /**
     * 归属平台：WECHAT/ALIPAY/MEITUAN/TIKTOK/NINGBO_BANK/xxx
     */
    @Column("platform_code")
    private String platformCode;

    /**
     * 状态：ACTIVE/INACTIVE
     */
    @Column("status")
    private String status;

    /**
     * 备注
     */
    @Column("remark")
    private String remark;
}
