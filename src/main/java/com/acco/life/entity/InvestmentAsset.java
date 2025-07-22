package com.acco.life.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Table("investment_asset")
@Data
public class InvestmentAsset {


    /**
     * 投资资产ID
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
     * 类型：1-stock(股票)，2-fund(基金)，3-bond(债券)
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
