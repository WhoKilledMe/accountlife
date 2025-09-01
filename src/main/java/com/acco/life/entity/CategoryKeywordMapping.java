package com.acco.life.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import org.springframework.data.relational.core.mapping.Table;
import org.springframework.data.relational.core.mapping.Column;

/**
 * 分类关键词映射实体
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = true)
@Table("category_keyword_mapping")
public class CategoryKeywordMapping extends BaseColumnEntity {
    
    @Column("category_id")
    private Integer categoryId;
    
    private String keyword;
    
    private Integer weight;
    
    @Column("user_id")
    private Integer userId;
    
    @Column("is_active")
    private Boolean isActive;
} 