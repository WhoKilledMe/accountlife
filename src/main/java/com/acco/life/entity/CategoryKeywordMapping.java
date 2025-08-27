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
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table("category_keyword_mapping")
public class CategoryKeywordMapping {
    
    @Id
    private Integer id;
    
    @Column("category_id")
    private Integer categoryId;
    
    private String keyword;
    
    private Integer weight;
    
    @Column("user_id")
    private Integer userId;
    
    @Column("is_active")
    private Boolean isActive;
    
    @Column("created_time")
    private LocalDateTime createdTime;
    
    @Column("updated_time")
    private LocalDateTime updatedTime;
} 