package com.acco.life.entity.fin;

import com.acco.life.entity.BaseColumnEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

/**
 * 账单映射规则实体
 * 用于配置化管理各平台账单字段到内部模型的映射关系
 *
 * @author wensen.zhang
 * @version V2.0.0
 */
@EqualsAndHashCode(callSuper = true)
@Table("fin_statement_mapping_rule")
@Data
public class FinStatementMappingRule extends BaseColumnEntity {

    /**
     * 平台代码：WECHAT/ALIPAY/MEITUAN/JD/CMB/ABC 等
     */
    @Column("platform_code")
    private String platformCode;

    /**
     * 来源字段类型：PAYMENT_METHOD/TRADE_CATEGORY/REMARK 等
     */
    @Column("source_type")
    private String sourceType;

    /**
     * 来源匹配模式：支持模糊匹配的关键字或模式（如 微信支付 / %京东白条%）
     */
    @Column("source_pattern")
    private String sourcePattern;

    /**
     * 目标类型：ACCOUNT_REF/PAYMENT_CODE/CATEGORY_ID 等
     */
    @Column("target_type")
    private String targetType;

    /**
     * 目标值：如 WECHAT_PAY/JD_BAITIAO/具体 categoryId 等
     */
    @Column("target_value")
    private String targetValue;

    /**
     * 匹配优先级，数值越大优先
     */
    @Column("priority")
    private Integer priority;

    /**
     * 是否启用：1-启用，0-禁用
     */
    @Column("enabled")
    private Boolean enabled;

    /**
     * 规则备注说明
     */
    @Column("remark")
    private String remark;
}

