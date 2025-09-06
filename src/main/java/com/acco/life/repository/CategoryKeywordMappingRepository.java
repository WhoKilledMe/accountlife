package com.acco.life.repository;

import com.acco.life.entity.CategoryKeywordMapping;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * 分类关键词映射Repository
 */
@Repository
public interface CategoryKeywordMappingRepository extends ReactiveCrudRepository<CategoryKeywordMapping, Long> {
    
    /**
     * 根据关键词模糊搜索（支持分词）
     */
    @Query("""
        SELECT * FROM category_keyword_mapping 
        WHERE is_active = true 
        AND (
            keyword LIKE CONCAT('%', :keyword, '%') 
            OR :keyword LIKE CONCAT('%', keyword, '%')
        )
        ORDER BY weight DESC, keyword ASC
        """)
    Flux<CategoryKeywordMapping> findByKeywordContaining(String keyword);
    
    /**
     * 根据分类ID查找
     */
    @Query("""
        SELECT * FROM category_keyword_mapping 
        WHERE category_id = :categoryId 
        AND is_active = true 
        ORDER BY weight DESC, keyword ASC
        """)
    Flux<CategoryKeywordMapping> findByCategoryId(Long categoryId);
    
    /**
     * 根据用户ID查找自定义关键词
     */
    @Query("""
        SELECT * FROM category_keyword_mapping 
        WHERE user_id = :userId 
        AND is_active = true 
        ORDER BY weight DESC, keyword ASC
        """)
    Flux<CategoryKeywordMapping> findByUserId(Long userId);
    
    /**
     * 根据关键词精确匹配
     */
    @Query("""
        SELECT * FROM category_keyword_mapping 
        WHERE keyword = :keyword 
        AND is_active = true 
        ORDER BY weight DESC
        LIMIT 1
        """)
    Mono<CategoryKeywordMapping> findByKeywordExact(String keyword);
    
    /**
     * 根据关键词和分类ID查找
     */
    @Query("""
        SELECT * FROM category_keyword_mapping 
        WHERE keyword = :keyword 
        AND category_id = :categoryId 
        AND is_active = true 
        ORDER BY weight DESC
        LIMIT 1
        """)
    Mono<CategoryKeywordMapping> findByKeywordAndCategoryId(String keyword, Long categoryId);
    
    /**
     * 获取所有启用的关键词
     */
    @Query("""
        SELECT DISTINCT keyword FROM category_keyword_mapping 
        WHERE is_active = true 
        ORDER BY keyword ASC
        """)
    Flux<String> findAllActiveKeywords();
    
    /**
     * 根据权重查找关键词
     */
    @Query("""
        SELECT * FROM category_keyword_mapping 
        WHERE weight >= :minWeight 
        AND is_active = true 
        ORDER BY weight DESC, keyword ASC
        """)
    Flux<CategoryKeywordMapping> findByWeightGreaterThanEqual(Integer minWeight);
} 