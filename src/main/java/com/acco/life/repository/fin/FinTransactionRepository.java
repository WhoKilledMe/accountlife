package com.acco.life.repository.fin;

import com.acco.life.entity.fin.FinTransaction;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

/**
 * 交易中枢表 Repository
 *
 * @author wensen.zhang
 * @version V2.0.0
 */
public interface FinTransactionRepository extends ReactiveCrudRepository<FinTransaction, Long> {

    /**
     * 根据交易号查询
     */
    Mono<FinTransaction> findByTransactionNo(String transactionNo);

    /**
     * 根据用户ID查询
     */
    Flux<FinTransaction> findByUserId(Long userId);

    /**
     * 根据用户ID和业务类型查询
     */
    Flux<FinTransaction> findByUserIdAndBizType(Long userId, String bizType);

    /**
     * 根据用户ID和状态查询
     */
    Flux<FinTransaction> findByUserIdAndStatus(Long userId, String status);

    /**
     * 根据用户ID和时间范围查询
     */
    @Query("""
        SELECT * FROM fin_transaction 
        WHERE user_id = :userId 
          AND trade_time >= :startTime 
          AND trade_time <= :endTime
          AND is_deleted = 0
        ORDER BY trade_time DESC
    """)
    Flux<FinTransaction> findByUserIdAndTimeRange(Long userId, LocalDateTime startTime, LocalDateTime endTime);

    /**
     * 根据原交易ID查询退款记录
     */
    Flux<FinTransaction> findByOriginalTransactionId(Long originalTransactionId);

    /**
     * 分页查询
     */
    @Query("""
        SELECT * FROM fin_transaction 
        WHERE (:userId IS NULL OR user_id = :userId)
          AND (:bizType IS NULL OR biz_type = :bizType)
          AND (:status IS NULL OR status = :status)
          AND (:platformCode IS NULL OR platform_code = :platformCode)
          AND (:categoryId IS NULL OR category_id = :categoryId)
          AND is_deleted = 0
        ORDER BY trade_time DESC
        LIMIT :limit OFFSET :offset
    """)
    Flux<FinTransaction> search(Long userId, String bizType, String status, String platformCode, Long categoryId, long limit, long offset);

    /**
     * 统计查询
     */
    @Query("""
        SELECT COUNT(1) FROM fin_transaction 
        WHERE (:userId IS NULL OR user_id = :userId)
          AND (:bizType IS NULL OR biz_type = :bizType)
          AND (:status IS NULL OR status = :status)
          AND (:platformCode IS NULL OR platform_code = :platformCode)
          AND (:categoryId IS NULL OR category_id = :categoryId)
          AND is_deleted = 0
    """)
    Mono<Long> countSearch(Long userId, String bizType, String status, String platformCode, Long categoryId);
}
