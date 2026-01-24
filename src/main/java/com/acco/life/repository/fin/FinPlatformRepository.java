package com.acco.life.repository.fin;

import com.acco.life.entity.fin.FinPlatform;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * 平台字典表 Repository
 *
 * @author wensen.zhang
 * @version V2.0.0
 */
public interface FinPlatformRepository extends ReactiveCrudRepository<FinPlatform, Long> {

    /**
     * 根据平台代码查询
     */
    Mono<FinPlatform> findByPlatformCode(String platformCode);

    /**
     * 根据平台类型查询
     */
    Flux<FinPlatform> findByPlatformType(String platformType);

    /**
     * 根据状态查询
     */
    Flux<FinPlatform> findByStatus(String status);

    /**
     * 查询所有启用的平台（按排序权重降序）
     */
    @Query("SELECT * FROM fin_platform WHERE status = 'ACTIVE' AND is_deleted = 0 ORDER BY sort_order DESC, id ASC")
    Flux<FinPlatform> findAllActive();

    /**
     * 根据平台代码和状态查询
     */
    Mono<FinPlatform> findByPlatformCodeAndStatus(String platformCode, String status);

    /**
     * 分页查询
     */
    @Query("""
        SELECT * FROM fin_platform 
        WHERE (:platformType IS NULL OR platform_type = :platformType)
          AND (:status IS NULL OR status = :status)
          AND is_deleted = 0
        ORDER BY sort_order DESC, id ASC
        LIMIT :limit OFFSET :offset
        """)
    Flux<FinPlatform> findPage(String platformType, String status, int limit, int offset);

    /**
     * 统计总数
     */
    @Query("""
        SELECT COUNT(*) FROM fin_platform 
        WHERE (:platformType IS NULL OR platform_type = :platformType)
          AND (:status IS NULL OR status = :status)
          AND is_deleted = 0
        """)
    Mono<Long> countSearch(String platformType, String status);
}
