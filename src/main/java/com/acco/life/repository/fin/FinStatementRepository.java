package com.acco.life.repository.fin;

import com.acco.life.entity.fin.FinStatement;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 账单行表 Repository
 *
 * @author wensen.zhang
 * @version V2.0.0
 */
public interface FinStatementRepository extends ReactiveCrudRepository<FinStatement, Long> {

    /**
     * 根据文件ID查询
     */
    Flux<FinStatement> findByFileId(Long fileId);

    /**
     * 根据文件ID和行Hash查询（幂等检查）
     */
    Mono<FinStatement> findByFileIdAndRawRowHash(Long fileId, String rawRowHash);

    /**
     * 根据用户ID查询
     */
    Flux<FinStatement> findByUserId(Long userId);

    /**
     * 根据用户ID和状态查询
     */
    Flux<FinStatement> findByUserIdAndStatus(Long userId, String status);

    /**
     * 根据用户ID和平台代码查询
     */
    Flux<FinStatement> findByUserIdAndPlatformCode(Long userId, String platformCode);

    /**
     * 根据平台订单号查询
     */
    Mono<FinStatement> findByOutTradeNo(String outTradeNo);

    /**
     * 根据用户ID、订单号、金额和时间范围查找匹配的账单（用于跨平台对账）
     */
    @Query("""
        SELECT * FROM fin_statement 
        WHERE user_id = :userId 
          AND (:orderNo IS NULL OR out_trade_no = :orderNo OR out_trade_no LIKE CONCAT('%', :orderNo, '%'))
          AND (:amount IS NULL OR ABS(amount - :amount) <= 0.1)
          AND (:startTime IS NULL OR stmt_time >= :startTime)
          AND (:endTime IS NULL OR stmt_time <= :endTime)
          AND (:excludePlatform IS NULL OR platform_code != :excludePlatform)
          AND status IN ('PARSED', 'MAPPED')
        ORDER BY stmt_time DESC
        LIMIT :limit
    """)
    Flux<FinStatement> findMatchingStatements(Long userId, String orderNo, BigDecimal amount, 
                                               LocalDateTime startTime, LocalDateTime endTime, 
                                               String excludePlatform, int limit);

    /**
     * 根据用户ID和时间范围查询
     */
    @Query("""
        SELECT * FROM fin_statement 
        WHERE user_id = :userId 
          AND stmt_time >= :startTime 
          AND stmt_time <= :endTime
        ORDER BY stmt_time DESC
    """)
    Flux<FinStatement> findByUserIdAndTimeRange(Long userId, LocalDateTime startTime, LocalDateTime endTime);

    /**
     * 查询待对账的账单（状态为NEW或PARSED）
     */
    @Query("SELECT * FROM fin_statement WHERE user_id = :userId AND status IN ('NEW', 'PARSED') ORDER BY stmt_time ASC")
    Flux<FinStatement> findPendingReconciliation(Long userId);

    /**
     * 分页查询
     */
    @Query("""
        SELECT * FROM fin_statement 
        WHERE (:userId IS NULL OR user_id = :userId)
          AND (:fileId IS NULL OR file_id = :fileId)
          AND (:platformCode IS NULL OR platform_code = :platformCode)
          AND (:status IS NULL OR status = :status)
        ORDER BY stmt_time DESC
        LIMIT :limit OFFSET :offset
    """)
    Flux<FinStatement> search(Long userId, Long fileId, String platformCode, String status, long limit, long offset);

    /**
     * 统计查询
     */
    @Query("""
        SELECT COUNT(1) FROM fin_statement 
        WHERE (:userId IS NULL OR user_id = :userId)
          AND (:fileId IS NULL OR file_id = :fileId)
          AND (:platformCode IS NULL OR platform_code = :platformCode)
          AND (:status IS NULL OR status = :status)
    """)
    Mono<Long> countSearch(Long userId, Long fileId, String platformCode, String status);
}
