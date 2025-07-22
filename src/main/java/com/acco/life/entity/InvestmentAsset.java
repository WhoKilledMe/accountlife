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
