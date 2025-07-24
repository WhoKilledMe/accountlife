package com.acco.life.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * description: 资产账户实体类，对应数据库表asset_account
 *
 * @date: 2025-07-24 17:54:23
 * @author wensen.zhang
 * @version V1.0.0
 */
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
     * 币种
     */
    
    @Column("currency")
    private String currency;

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
     * 修改人
     */
    
    @Column("updated_by")
    private String updatedBy;

    /**
     * 修改时间
     */
    
    @Column("updated_at")
    private LocalDateTime updatedAt;

    /**
     * 是否删除（0-否，1-是）
     */
    
    @Column("is_deleted")
    private Integer isDeleted;
}
