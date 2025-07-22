package com.acco.life.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

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
