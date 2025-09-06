package com.acco.life.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * description: 固定资产实体类，对应数据库表fixed_asset
 *
 * @date: 2025-07-24 17:54:23
 * @author wensen.zhang
 * @version V1.0.0
 */
@EqualsAndHashCode(callSuper = true)
@Table("fixed_asset")
@Data
public class FixedAsset extends BaseColumnEntity {

    /**
     * 所属用户
     */
    @Column("user_id")
    private Long userId;

    /**
     * 资产名称
     */
    @Column("name")
    private String name;

    /**
     * 资产类型：1-property，2-vehicle，3-equipment，4-other
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
}
