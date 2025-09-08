package com.acco.life.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

/**
 * 账户配置实体类（系统级）
 *
 * @author wensen.zhang
 * @version V1.0.0
 */
@EqualsAndHashCode(callSuper = true)
@Table("account_config")
@Data
public class AccountConfig extends BaseColumnEntity {

    /**
     * 账户名称（如 招商银行、支付宝）
     */
    @Column("name")
    private String name;

    /**
     * 账户类型：1-bank，2-platform，3-credit_wallet，4-wallet，5-insurance，6-securities
     */
    @Column("type")
    private Integer type;

    /**
     * 平台标识（如 ALIPAY、MEITUAN）
     */
    @Column("platform_code")
    private String platformCode;

    /**
     * 官网登录地址
     */
    @Column("website_url")
    private String websiteUrl;

    /**
     * 描述信息
     */
    @Column("description")
    private String description;

    /**
     * Logo图片地址
     */
    @Column("logo_url")
    private String logoUrl;

    /**
     * 排序权重
     */
    @Column("sort_order")
    private Integer sortOrder;

    /**
     * 是否启用
     */
    @Column("is_active")
    private Boolean isActive;

    /**
     * 账单邮箱地址
     */
    @Column("bill_email")
    private String billEmail;
}
