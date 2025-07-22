package com.acco.life.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Table("asset_account")
@Data
public class AssetAccount {


    /**
     * 账户ID
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
     * 账户名称（如 招商银行、花呗）
     */
    
    @Column("name")
    private String name;

    /**
     * 账户类型：1-bank(银行)，2-platform(平台)，3-credit_wallet(信用钱包)，4-wallet(虚拟余额)
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
     * 是否为虚拟账户（如信用钱包）
     */
    
    @Column("is_virtual")
    private String isVirtual;

    /**
     * 信用额度，仅信用钱包用
     */
    
    @Column("credit_limit")
    private BigDecimal creditLimit;

    /**
     * 币种
     */
    
    @Column("currency")
    private String currency;

    /**
     * 创建时间
     */
    
    @Column("created_at")
    private LocalDateTime createdAt;
}
