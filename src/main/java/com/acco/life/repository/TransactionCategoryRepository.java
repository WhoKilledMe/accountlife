package com.acco.life.repository;

import com.acco.life.entity.TransactionCategory;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

/**
 * description: 交易分类仓库接口，提供交易分类的数据库操作
 *
 * @date: 2025-07-24 17:54:23
 * @author wensen.zhang
 * @version V1.0.0
 */
public interface TransactionCategoryRepository extends ReactiveCrudRepository<TransactionCategory, Integer> {
    
    /**
     * 根据分类名称、类型和用户ID查找分类
     * 优先返回用户自定义分类，如果没有则返回系统分类
     */
    @Query("""
        SELECT * FROM transaction_category 
        WHERE name = :name AND type = :type 
        AND (user_id = :userId OR user_id IS NULL)
        ORDER BY CASE WHEN user_id IS NOT NULL THEN 0 ELSE 1 END
        LIMIT 1
        """)
    Mono<TransactionCategory> findByNameAndTypeAndUserId(String name, Integer type, Integer userId);
    
    /**
     * 根据分类类型和用户ID查找分类（保持向后兼容）
     */
    @Query("""
        SELECT * FROM transaction_category 
        WHERE name = :name AND type = :type 
        AND (user_id = :userId OR user_id IS NULL)
        ORDER BY CASE WHEN user_id IS NOT NULL THEN 0 ELSE 1 END
        LIMIT 1
        """)
    Mono<TransactionCategory> findByTypeAndUserId(Integer type, String name, Integer userId);
}
