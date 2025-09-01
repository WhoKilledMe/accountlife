package com.acco.life.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * description: 投资资产实体类，对应数据库表investment_asset
 *
 * @date: 2025-07-24 17:54:23
 * @author wensen.zhang
 * @version V1.0.0
 */
@EqualsAndHashCode(callSuper = true)
@Table("investment_asset")
@Data
public class InvestmentAsset extends BaseColumnEntity {

    /**
     * 所属用户
     */
    @Column("user_id")
    private Integer userId;

    /**
     * 关联账户
     */
    @Column("account_id")
    private Integer accountId;

    /**
     * 股票/基金代码
     */
    @Column("code")
    private String code;

    /**
     * 名称
     */
    @Column("name")
    private String name;

    /**
     * 类型：1-stock，2-fund，3-bond
     */
    @Column("type")
    private Integer type;

    /**
     * 持仓数量
     */
    @Column("quantity")
    private BigDecimal quantity;

    /**
     * 成本价
     */
    @Column("cost_price")
    private BigDecimal costPrice;

    /**
     * 市价
     */
    @Column("market_price")
    private BigDecimal marketPrice;

    /**
     * 币种
     */
    @Column("currency")
    private String currency;

    /**
     * 更新时间
     */
    @Column("last_updated")
    private LocalDateTime lastUpdated;
}
