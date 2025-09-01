package com.acco.life.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

/**
 * description: 交易分类实体类，对应数据库表transaction_category
 *
 * @date: 2025-07-24 17:54:23
 * @author wensen.zhang
 * @version V1.0.0
 */
@EqualsAndHashCode(callSuper = true)
@Table("transaction_category")
@Data
public class TransactionCategory extends BaseColumnEntity {

    /**
     * 分类名称（如 餐饮）
     */
    @Column("name")
    private String name;

    /**
     * 分类类型：1-income，2-expense
     */
    @Column("type")
    private Integer type;

    /**
     * 父分类ID
     */
    @Column("parent_id")
    private Integer parentId;

    /**
     * 图标
     */
    @Column("icon")
    private String icon;

    /**
     * 所属用户，null 表示系统分类
     */
    @Column("user_id")
    private Integer userId;

    /**
     * 排序值
     */
    @Column("sort_order")
    private Integer sortOrder;
}
