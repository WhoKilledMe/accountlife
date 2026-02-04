package com.acco.life.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;
import org.springframework.data.relational.core.mapping.Column;

import java.time.LocalDateTime;

/**
 * 分类关键词映射实体
 * 注意：此表使用 created_time/updated_time，而非 BaseColumnEntity 的 created_at/updated_at
 * 因此不继承 BaseColumnEntity，直接定义所需字段
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table("category_keyword_mapping")
public class CategoryKeywordMapping {
    
    @Id
    @Column("id")
    private Long id;
    
    @Column("category_id")
    private Long categoryId;
    
    private String keyword;
    
    private Integer weight;
    
    @Column("user_id")
    private Long userId;
    
    @Column("is_active")
    private Boolean isActive;
    
    /**
     * 创建时间（映射到 created_time）
     */
    @Column("created_time")
    private LocalDateTime createdTime;
    
    /**
     * 更新时间（映射到 updated_time）
     */
    @Column("updated_time")
    private LocalDateTime updatedTime;
} 