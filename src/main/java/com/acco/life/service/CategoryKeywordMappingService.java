package com.acco.life.service;

import com.acco.life.entity.CategoryKeywordMapping;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * 分类关键词映射Service接口
 */
public interface CategoryKeywordMappingService {
    
    /**
     * 根据关键词智能匹配分类ID（优先用户自定义，其次系统）
     */
    Mono<Integer> findCategoryIdByKeyword(String keyword, Integer userId);
    
    /**
     * 根据关键词模糊搜索
     */
    Flux<CategoryKeywordMapping> searchByKeyword(String keyword);
    
    /**
     * 根据分类ID查找关键词
     */
    Flux<CategoryKeywordMapping> findByCategoryId(Integer categoryId);
    
    /**
     * 根据用户ID查找自定义关键词
     */
    Flux<CategoryKeywordMapping> findByUserId(Integer userId);
    
    /**
     * 添加关键词映射
     */
    Mono<CategoryKeywordMapping> addKeywordMapping(CategoryKeywordMapping mapping);
    
    /**
     * 更新关键词映射
     */
    Mono<CategoryKeywordMapping> updateKeywordMapping(CategoryKeywordMapping mapping);
    
    /**
     * 删除关键词映射
     */
    Mono<Void> deleteKeywordMapping(Integer id);
    
    /**
     * 批量添加关键词映射
     */
    Flux<CategoryKeywordMapping> batchAddKeywordMappings(Flux<CategoryKeywordMapping> mappings);
    
    /**
     * 获取所有启用的关键词
     */
    Flux<String> getAllActiveKeywords();
    
    /**
     * 根据权重查找关键词
     */
    Flux<CategoryKeywordMapping> findByWeightGreaterThanEqual(Integer minWeight);
} 