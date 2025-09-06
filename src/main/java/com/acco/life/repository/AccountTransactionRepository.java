package com.acco.life.repository;

import com.acco.life.entity.AccountTransaction;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.data.r2dbc.repository.Query;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * description: 账户交易仓库接口，提供账户交易的数据库操作
 *
 * @date: 2025-07-24 17:54:23
 * @author wensen.zhang
 * @version V1.0.0
 */
public interface AccountTransactionRepository extends ReactiveCrudRepository<AccountTransaction, Long> {

    @Query("""
        SELECT *
        FROM account_transaction
        WHERE (:description IS NULL OR description LIKE :description)
          AND (:userId IS NULL OR user_id = :userId)
          AND (:accountId IS NULL OR account_id = :accountId)
        ORDER BY id DESC
        LIMIT :limit OFFSET :offset
    """)
    Flux<AccountTransaction> search(String description, Long userId, Long accountId, long limit, long offset);

    @Query("""
        SELECT COUNT(1)
        FROM account_transaction
        WHERE (:description IS NULL OR description LIKE :description)
          AND (:userId IS NULL OR user_id = :userId)
          AND (:accountId IS NULL OR account_id = :accountId)
    """)
    Mono<Long> countSearch(String description, Long userId, Long accountId);
}
