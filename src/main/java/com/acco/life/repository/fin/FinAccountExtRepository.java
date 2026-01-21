package com.acco.life.repository.fin;

import com.acco.life.entity.fin.FinAccountExt;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * 账户扩展表 Repository
 *
 * @author wensen.zhang
 * @version V2.0.0
 */
public interface FinAccountExtRepository extends ReactiveCrudRepository<FinAccountExt, Long> {

    /**
     * 根据账户ID查询扩展信息
     */
    Mono<FinAccountExt> findByAccountId(Long accountId);

    /**
     * 根据账户ID列表查询扩展信息
     */
    Flux<FinAccountExt> findByAccountIdIn(Iterable<Long> accountIds);

    /**
     * 根据账户ID删除扩展信息
     */
    Mono<Void> deleteByAccountId(Long accountId);
}
