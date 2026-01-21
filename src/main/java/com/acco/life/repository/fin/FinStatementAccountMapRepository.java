package com.acco.life.repository.fin;

import com.acco.life.entity.fin.FinStatementAccountMap;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * 账单-账户映射表 Repository
 *
 * @author wensen.zhang
 * @version V2.0.0
 */
public interface FinStatementAccountMapRepository extends ReactiveCrudRepository<FinStatementAccountMap, Long> {

    /**
     * 根据账单ID查询映射
     */
    Flux<FinStatementAccountMap> findByStatementId(Long statementId);

    /**
     * 根据账户ID查询映射
     */
    Flux<FinStatementAccountMap> findByAccountId(Long accountId);

    /**
     * 根据账单ID和账户ID查询
     */
    Mono<FinStatementAccountMap> findByStatementIdAndAccountId(Long statementId, Long accountId);

    /**
     * 根据账单ID删除映射
     */
    Mono<Void> deleteByStatementId(Long statementId);

    /**
     * 根据映射类型查询
     */
    Flux<FinStatementAccountMap> findByMapType(String mapType);
}
