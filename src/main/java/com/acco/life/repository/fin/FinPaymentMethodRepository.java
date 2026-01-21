package com.acco.life.repository.fin;

import com.acco.life.entity.fin.FinPaymentMethod;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * 支付方式表 Repository
 *
 * @author wensen.zhang
 * @version V2.0.0
 */
public interface FinPaymentMethodRepository extends ReactiveCrudRepository<FinPaymentMethod, Long> {

    /**
     * 根据支付方式代码查询
     */
    Mono<FinPaymentMethod> findByPaymentCode(String paymentCode);

    /**
     * 根据平台代码查询
     */
    Flux<FinPaymentMethod> findByPlatformCode(String platformCode);

    /**
     * 根据支付类别查询
     */
    Flux<FinPaymentMethod> findByPaymentType(String paymentType);

    /**
     * 查询所有启用的支付方式
     */
    @Query("SELECT * FROM fin_payment_method WHERE status = 'ACTIVE'")
    Flux<FinPaymentMethod> findAllActive();

    /**
     * 根据平台代码查询启用的支付方式
     */
    @Query("SELECT * FROM fin_payment_method WHERE platform_code = :platformCode AND status = 'ACTIVE'")
    Flux<FinPaymentMethod> findActiveByPlatformCode(String platformCode);
}
