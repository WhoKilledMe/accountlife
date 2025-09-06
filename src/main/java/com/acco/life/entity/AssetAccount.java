package com.acco.life.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;

/**
 * description: 资产账户实体类，对应数据库表asset_account
 *
 * @date: 2025-07-24 17:54:23
 * @author wensen.zhang
 * @version V1.0.0
 */
@EqualsAndHashCode(callSuper = true)
@Table("asset_account")
@Data
public class AssetAccount extends BaseColumnEntity {

    /**
     * 所属用户
     */
    @Column("user_id")
    private Long userId;

    /**
     * 账户名称（如 招商银行、花呗）
     */
    @Column("name")
    private String name;

    /**
     * 账户类型：1-bank，2-platform，3-credit_wallet，4-wallet
     */
    @Column("type")
    private Integer type;

    /**
     * 平台标识（如 ALIPAY、MEITUAN）
     */
    @Column("platform_code")
    private String platformCode;

    /**
     * 银行卡号/平台账号
     */
    @Column("account_number")
    private String accountNumber;

    /**
     * 是否为虚拟账户
     */
    @Column("is_virtual")
    private Boolean isVirtual;

    /**
     * 信用额度，仅信用钱包用
     */
    @Column("credit_limit")
    private BigDecimal creditLimit;

    /**
     * 账户余额
     */
    @Column("balance")
    private BigDecimal balance;

    /**
     * 币种
     */
    @Column("currency")
    private String currency;
}
