package com.acco.life.repository.fin;

import com.acco.life.entity.fin.FinPaymentRoute;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * 支付拆分表 Repository
 *
 * @author wensen.zhang
 * @version V2.0.0
 */
public interface FinPaymentRouteRepository extends ReactiveCrudRepository<FinPaymentRoute, Long> {

    /**
     * 根据交易ID查询（按顺序）
     */
    @Query("SELECT * FROM fin_payment_route WHERE transaction_id = :transactionId ORDER BY route_order ASC")
    Flux<FinPaymentRoute> findByTransactionIdOrderByRouteOrder(Long transactionId);

    /**
     * 根据交易ID查询
     */
    Flux<FinPaymentRoute> findByTransactionId(Long transactionId);

    /**
     * 根据来源账户ID查询
     */
    Flux<FinPaymentRoute> findByFromAccountId(Long fromAccountId);

    /**
     * 根据目标账户ID查询
     */
    Flux<FinPaymentRoute> findByToAccountId(Long toAccountId);

    /**
     * 根据支付方式ID查询
     */
    Flux<FinPaymentRoute> findByPaymentMethodId(Long paymentMethodId);

    /**
     * 根据交易ID删除
     */
    Mono<Void> deleteByTransactionId(Long transactionId);

    /**
     * 根据路由类型查询
     */
    Flux<FinPaymentRoute> findByRouteType(String routeType);
}
