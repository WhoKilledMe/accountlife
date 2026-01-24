package com.acco.life.entity.fin;

import com.acco.life.entity.BaseColumnEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

/**
 * 平台字典表实体类
 * 统一管理所有平台/机构的基础信息及能力
 *
 * @author wensen.zhang
 * @version V2.0.0
 */
@EqualsAndHashCode(callSuper = true)
@Table("fin_platform")
@Data
public class FinPlatform extends BaseColumnEntity {

    /**
     * 平台唯一编码：WECHAT/ALIPAY/MEITUAN/CMB/ICBC/CCB/NINGBO/TIKTOK/TONGHUASHUN 等
     */
    @Column("platform_code")
    private String platformCode;

    /**
     * 平台名称：微信、支付宝、美团、招商银行等
     */
    @Column("platform_name")
    private String platformName;

    /**
     * 平台类型：PAYMENT/MERCHANT/BANK/BROKER/E_WALLET/INVESTMENT/OTHER
     */
    @Column("platform_type")
    private String platformType;

    /**
     * 是否提供支付能力（1-支持，0-不支持）
     */
    @Column("support_payment")
    private Boolean supportPayment;

    /**
     * 是否提供信用能力（花呗/月付/信用卡，1-支持，0-不支持）
     */
    @Column("support_credit")
    private Boolean supportCredit;

    /**
     * 是否提供余额账户（1-支持，0-不支持）
     */
    @Column("support_balance")
    private Boolean supportBalance;

    /**
     * 是否支持账单导出/导入（1-支持，0-不支持）
     */
    @Column("support_bill")
    private Boolean supportBill;

    /**
     * 支持的账单格式：CSV/JSON/PDF/EMAIL/XLSX
     */
    @Column("bill_import_format")
    private String billImportFormat;

    /**
     * 账单邮箱域名（用于自动识别，如 bill@alipay.com）
     */
    @Column("bill_email_domain")
    private String billEmailDomain;

    /**
     * 平台Logo地址（用于UI展示）
     */
    @Column("logo_url")
    private String logoUrl;

    /**
     * 平台官网地址
     */
    @Column("website_url")
    private String websiteUrl;

    /**
     * API配置信息（JSON格式，如接口地址、认证方式等，备用）
     */
    @Column("api_config")
    private String apiConfig;

    /**
     * 平台描述信息
     */
    @Column("description")
    private String description;

    /**
     * 排序权重（数值越大越靠前）
     */
    @Column("sort_order")
    private Integer sortOrder;

    /**
     * 状态：ACTIVE/INACTIVE
     */
    @Column("status")
    private String status;
}
