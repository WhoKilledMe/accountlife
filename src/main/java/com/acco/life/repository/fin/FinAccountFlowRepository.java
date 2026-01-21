package com.acco.life.repository.fin;

import com.acco.life.entity.fin.FinAccountFlow;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

/**
 * 总账流水表 Repository
 *
 * @author wensen.zhang
 * @version V2.0.0
 */
public interface FinAccountFlowRepository extends ReactiveCrudRepository<FinAccountFlow, Long> {

    /**
     * 根据流水号查询
     */
    Mono<FinAccountFlow> findByFlowNo(String flowNo);

    /**
     * 根据用户ID查询
     */
    Flux<FinAccountFlow> findByUserId(Long userId);

    /**
     * 根据账户ID查询
     */
    Flux<FinAccountFlow> findByAccountId(Long accountId);

    /**
     * 根据交易ID查询
     */
    Flux<FinAccountFlow> findByTransactionId(Long transactionId);

    /**
     * 根据会计分录组ID查询（用于验证借贷平衡）
     */
    Flux<FinAccountFlow> findByJournalId(Long journalId);

    /**
     * 根据账户ID和时间范围查询
     */
    @Query("""
        SELECT * FROM fin_account_flow 
        WHERE account_id = :accountId 
          AND trade_time >= :startTime 
          AND trade_time <= :endTime
        ORDER BY trade_time ASC
    """)
    Flux<FinAccountFlow> findByAccountIdAndTimeRange(Long accountId, LocalDateTime startTime, LocalDateTime endTime);

    /**
     * 根据用户ID和时间范围查询
     */
    @Query("""
        SELECT * FROM fin_account_flow 
        WHERE user_id = :userId 
          AND trade_time >= :startTime 
          AND trade_time <= :endTime
        ORDER BY trade_time DESC
    """)
    Flux<FinAccountFlow> findByUserIdAndTimeRange(Long userId, LocalDateTime startTime, LocalDateTime endTime);

    /**
     * 根据账户ID和方向查询
     */
    Flux<FinAccountFlow> findByAccountIdAndDirection(Long accountId, String direction);

    /**
     * 分页查询
     */
    @Query("""
        SELECT * FROM fin_account_flow 
        WHERE (:userId IS NULL OR user_id = :userId)
          AND (:accountId IS NULL OR account_id = :accountId)
          AND (:direction IS NULL OR direction = :direction)
          AND (:bizType IS NULL OR biz_type = :bizType)
        ORDER BY trade_time DESC
        LIMIT :limit OFFSET :offset
    """)
    Flux<FinAccountFlow> search(Long userId, Long accountId, String direction, String bizType, long limit, long offset);

    /**
     * 统计查询
     */
    @Query("""
        SELECT COUNT(1) FROM fin_account_flow 
        WHERE (:userId IS NULL OR user_id = :userId)
          AND (:accountId IS NULL OR account_id = :accountId)
          AND (:direction IS NULL OR direction = :direction)
          AND (:bizType IS NULL OR biz_type = :bizType)
    """)
    Mono<Long> countSearch(Long userId, Long accountId, String direction, String bizType);
}
