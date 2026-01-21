package com.acco.life.repository.fin;

import com.acco.life.entity.fin.FinClearingFlow;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

/**
 * 清算流水表 Repository
 *
 * @author wensen.zhang
 * @version V2.0.0
 */
public interface FinClearingFlowRepository extends ReactiveCrudRepository<FinClearingFlow, Long> {

    /**
     * 根据流水号查询
     */
    Mono<FinClearingFlow> findByFlowNo(String flowNo);

    /**
     * 根据用户ID查询
     */
    Flux<FinClearingFlow> findByUserId(Long userId);

    /**
     * 根据交易ID查询
     */
    Flux<FinClearingFlow> findByTransactionId(Long transactionId);

    /**
     * 根据用户ID和状态查询
     */
    Flux<FinClearingFlow> findByUserIdAndStatus(Long userId, String status);

    /**
     * 根据来源账户ID查询
     */
    Flux<FinClearingFlow> findByFromAccountId(Long fromAccountId);

    /**
     * 根据目标账户ID查询
     */
    Flux<FinClearingFlow> findByToAccountId(Long toAccountId);

    /**
     * 根据用户ID和时间范围查询
     */
    @Query("""
        SELECT * FROM fin_clearing_flow 
        WHERE user_id = :userId 
          AND trade_time >= :startTime 
          AND trade_time <= :endTime
        ORDER BY trade_time DESC
    """)
    Flux<FinClearingFlow> findByUserIdAndTimeRange(Long userId, LocalDateTime startTime, LocalDateTime endTime);
}
