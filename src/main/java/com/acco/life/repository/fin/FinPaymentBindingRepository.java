package com.acco.life.repository.fin;

import com.acco.life.entity.fin.FinPaymentBinding;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * 支付方式绑定表 Repository
 *
 * @author wensen.zhang
 * @version V2.0.0
 */
public interface FinPaymentBindingRepository extends ReactiveCrudRepository<FinPaymentBinding, Long> {

    /**
     * 根据支付方式ID查询绑定
     */
    Flux<FinPaymentBinding> findByPaymentMethodId(Long paymentMethodId);

    /**
     * 根据账户ID查询绑定
     */
    Flux<FinPaymentBinding> findByAccountId(Long accountId);

    /**
     * 根据支付方式ID和账户ID查询
     */
    Mono<FinPaymentBinding> findByPaymentMethodIdAndAccountId(Long paymentMethodId, Long accountId);

    /**
     * 根据支付方式ID查询启用的绑定（按优先级排序）
     */
    @Query("""
        SELECT * FROM fin_payment_binding 
        WHERE payment_method_id = :paymentMethodId AND enabled = 1 
        ORDER BY priority DESC
    """)
    Flux<FinPaymentBinding> findEnabledByPaymentMethodIdOrderByPriority(Long paymentMethodId);

    /**
     * 根据账户ID查询启用的绑定
     */
    @Query("SELECT * FROM fin_payment_binding WHERE account_id = :accountId AND enabled = 1")
    Flux<FinPaymentBinding> findEnabledByAccountId(Long accountId);
}
