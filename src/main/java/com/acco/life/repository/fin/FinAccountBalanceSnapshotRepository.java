package com.acco.life.repository.fin;

import com.acco.life.entity.fin.FinAccountBalanceSnapshot;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;

/**
 * 账户余额快照表 Repository
 *
 * @author wensen.zhang
 * @version V2.0.0
 */
public interface FinAccountBalanceSnapshotRepository extends ReactiveCrudRepository<FinAccountBalanceSnapshot, Long> {

    /**
     * 根据账户ID和快照日期查询
     */
    Mono<FinAccountBalanceSnapshot> findByAccountIdAndSnapshotDate(Long accountId, LocalDate snapshotDate);

    /**
     * 根据账户ID查询所有快照
     */
    Flux<FinAccountBalanceSnapshot> findByAccountIdOrderBySnapshotDateDesc(Long accountId);

    /**
     * 根据账户ID和日期范围查询快照
     */
    @Query("""
        SELECT * FROM fin_account_balance_snapshot 
        WHERE account_id = :accountId 
          AND snapshot_date >= :startDate 
          AND snapshot_date <= :endDate
        ORDER BY snapshot_date ASC
    """)
    Flux<FinAccountBalanceSnapshot> findByAccountIdAndDateRange(Long accountId, LocalDate startDate, LocalDate endDate);

    /**
     * 获取账户最新的快照
     */
    @Query("""
        SELECT * FROM fin_account_balance_snapshot 
        WHERE account_id = :accountId 
        ORDER BY snapshot_date DESC 
        LIMIT 1
    """)
    Mono<FinAccountBalanceSnapshot> findLatestByAccountId(Long accountId);
}
