package com.acco.life.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Table("fixed_asset")
@Data
public class FixedAsset {


    /**
     * 固定资产ID
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
     * 资产名称
     */
    
    @Column("name")
    private String name;

    /**
     * 资产类型：1-property(房产)，2-vehicle(车辆)，3-equipment(设备)，4-other(其他)
     */
    
    @Column("type")
    private Integer type;

    /**
     * 评估/购入金额
     */
    
    @Column("value")
    private BigDecimal value;

    /**
     * 购买日期
     */
    
    @Column("purchase_date")
    private LocalDateTime purchaseDate;

    /**
     * 地址或位置
     */
    
    @Column("location")
    private String location;

    /**
     * 备注
     */
    
    @Column("note")
    private String note;

    /**
     * 创建时间
     */
    
    @Column("created_at")
    private LocalDateTime createdAt;
}
