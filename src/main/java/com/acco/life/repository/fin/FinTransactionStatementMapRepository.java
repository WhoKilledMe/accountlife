package com.acco.life.repository.fin;

import com.acco.life.entity.fin.FinTransactionStatementMap;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * 交易-账单映射表 Repository
 *
 * @author wensen.zhang
 * @version V2.0.0
 */
public interface FinTransactionStatementMapRepository extends ReactiveCrudRepository<FinTransactionStatementMap, Long> {

    /**
     * 根据交易ID查询映射
     */
    Flux<FinTransactionStatementMap> findByTransactionId(Long transactionId);

    /**
     * 根据账单ID查询映射
     */
    Flux<FinTransactionStatementMap> findByStatementId(Long statementId);

    /**
     * 根据交易ID和账单ID查询
     */
    Mono<FinTransactionStatementMap> findByTransactionIdAndStatementId(Long transactionId, Long statementId);

    /**
     * 根据交易ID删除映射
     */
    Mono<Void> deleteByTransactionId(Long transactionId);

    /**
     * 根据账单ID删除映射
     */
    Mono<Void> deleteByStatementId(Long statementId);

    /**
     * 根据映射类型查询
     */
    Flux<FinTransactionStatementMap> findByMapType(String mapType);

    /**
     * 根据确认状态查询
     */
    Flux<FinTransactionStatementMap> findByConfirmStatus(String confirmStatus);
}
