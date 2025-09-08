package com.acco.life.repository;

import com.acco.life.entity.AccountConfig;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * 账户配置仓库接口
 *
 * @author wensen.zhang
 * @version V1.0.0
 */
public interface AccountConfigRepository extends ReactiveCrudRepository<AccountConfig, Long> {
    
    /**
     * 根据类型查询启用的配置，按排序权重和ID排序
     */
    @Query("""
        SELECT * FROM account_config 
        WHERE type = :type AND is_active = true AND is_deleted = 0
        ORDER BY sort_order ASC, id ASC
    """)
    Flux<AccountConfig> findByTypeAndActiveOrderBySortOrder(Integer type);
    
    /**
     * 查询所有启用的配置，按类型、排序权重和ID排序
     */
    @Query("""
        SELECT * FROM account_config 
        WHERE is_active = true AND is_deleted = 0
        ORDER BY type ASC, sort_order ASC, id ASC
    """)
    Flux<AccountConfig> findAllActiveOrderByTypeAndSortOrder();
    
    /**
     * 根据名称模糊查询启用的配置
     */
    @Query("""
        SELECT * FROM account_config 
        WHERE is_active = true AND is_deleted = 0 
        AND (:nameLike IS NULL OR name LIKE :nameLike)
        ORDER BY type ASC, sort_order ASC, id ASC
    """)
    Flux<AccountConfig> findActiveByNameLikeOrderByTypeAndSortOrder(String nameLike);

    /**
     * 分页搜索系统账户配置
     */
    @Query("""
        SELECT *
        FROM account_config
        WHERE is_deleted = 0
          AND (:nameLike IS NULL OR name LIKE :nameLike)
          AND (:type IS NULL OR type = :type)
          AND (:platformLike IS NULL OR platform_code LIKE :platformLike)
          AND (:active IS NULL OR is_active = :active)
        ORDER BY type ASC, sort_order ASC, id DESC
        LIMIT :limit OFFSET :offset
    """)
    Flux<AccountConfig> search(String nameLike, Integer type, String platformLike, Boolean active, long limit, long offset);

    /**
     * 统计分页搜索总数
     */
    @Query("""
        SELECT COUNT(1)
        FROM account_config
        WHERE is_deleted = 0
          AND (:nameLike IS NULL OR name LIKE :nameLike)
          AND (:type IS NULL OR type = :type)
          AND (:platformLike IS NULL OR platform_code LIKE :platformLike)
          AND (:active IS NULL OR is_active = :active)
    """)
    Mono<Long> countSearch(String nameLike, Integer type, String platformLike, Boolean active);
}
