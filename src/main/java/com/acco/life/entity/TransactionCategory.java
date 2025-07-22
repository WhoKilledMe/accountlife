package com.acco.life.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Table("transaction_category")
@Data
public class TransactionCategory {


    /**
     * 分类ID
     */
    @Id
    @Column("id")
    private Integer id;

    /**
     * 分类名称（如 餐饮）
     */
    
    @Column("name")
    private String name;

    /**
     * 分类类型：1-income(收入)，2-expense(支出)
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
