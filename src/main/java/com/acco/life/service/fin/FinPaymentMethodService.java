package com.acco.life.service.fin;

import com.acco.life.dto.fin.FinPaymentBindingDto;
import com.acco.life.dto.fin.FinPaymentMethodDto;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * 支付方式管理服务接口
 *
 * @author wensen.zhang
 * @version V2.0.0
 */
public interface FinPaymentMethodService {

    /**
     * 创建支付方式
     */
    Mono<FinPaymentMethodDto> create(FinPaymentMethodDto dto);

    /**
     * 更新支付方式
     */
    Mono<FinPaymentMethodDto> update(FinPaymentMethodDto dto);

    /**
     * 根据ID查询
     */
    Mono<FinPaymentMethodDto> findById(Long id);

    /**
     * 根据支付方式代码查询
     */
    Mono<FinPaymentMethodDto> findByPaymentCode(String paymentCode);

    /**
     * 查询所有启用的支付方式
     */
    Mono<List<FinPaymentMethodDto>> findAllActive();

    /**
     * 根据平台代码查询启用的支付方式
     */
    Mono<List<FinPaymentMethodDto>> findActiveByPlatformCode(String platformCode);

    /**
     * 删除支付方式
     */
    Mono<Void> deleteById(Long id);

    /**
     * 绑定支付方式与账户
     */
    Mono<FinPaymentBindingDto> bindAccount(Long paymentMethodId, Long accountId, Integer priority);

    /**
     * 解绑支付方式与账户
     */
    Mono<Void> unbindAccount(Long paymentMethodId, Long accountId);

    /**
     * 查询支付方式绑定的账户
     */
    Mono<List<FinPaymentBindingDto>> findBindingsByPaymentMethodId(Long paymentMethodId);

    /**
     * 查询账户绑定的支付方式
     */
    Mono<List<FinPaymentBindingDto>> findBindingsByAccountId(Long accountId);
}
