package com.acco.life.repository;

import com.acco.life.entity.BusinessTransaction;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.data.r2dbc.repository.Query;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * description: 业务交易仓库接口，提供业务交易的数据库操作
 *
 * @date: 2025-01-15 10:00:00
 * @author wensen.zhang
 * @version V1.0.0
 */
public interface BusinessTransactionRepository extends ReactiveCrudRepository<BusinessTransaction, Long> {

    @Query("""
        SELECT *
        FROM business_transaction
        WHERE (:userId IS NULL OR user_id = :userId)
          AND (:categoryId IS NULL OR category_id = :categoryId)
        ORDER BY id DESC
        LIMIT :limit OFFSET :offset
    """)
    Flux<BusinessTransaction> search(Long userId, Long categoryId, long limit, long offset);

    @Query("""
        SELECT COUNT(1)
        FROM business_transaction
        WHERE (:userId IS NULL OR user_id = :userId)
          AND (:categoryId IS NULL OR category_id = :categoryId)
    """)
    Mono<Long> countSearch(Long userId, Long categoryId);
}
