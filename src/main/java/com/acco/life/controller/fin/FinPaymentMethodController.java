package com.acco.life.controller.fin;

import com.acco.life.dto.fin.FinPaymentBindingDto;
import com.acco.life.dto.fin.FinPaymentMethodDto;
import com.acco.life.service.fin.FinPaymentMethodService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * 支付方式管理 Controller
 *
 * @author wensen.zhang
 * @version V2.0.0
 */
@Slf4j
@RestController
@RequestMapping("/api/fin/payment-method")
@RequiredArgsConstructor
public class FinPaymentMethodController {

    private final FinPaymentMethodService paymentMethodService;

    /**
     * 创建支付方式
     */
    @PostMapping
    public Mono<ResponseEntity<FinPaymentMethodDto>> create(@RequestBody FinPaymentMethodDto dto) {
        return paymentMethodService.create(dto)
                .map(ResponseEntity::ok);
    }

    /**
     * 更新支付方式
     */
    @PutMapping
    public Mono<ResponseEntity<FinPaymentMethodDto>> update(@RequestBody FinPaymentMethodDto dto) {
        return paymentMethodService.update(dto)
                .map(ResponseEntity::ok);
    }

    /**
     * 根据ID查询
     */
    @GetMapping("/{id}")
    public Mono<ResponseEntity<FinPaymentMethodDto>> findById(@PathVariable Long id) {
        return paymentMethodService.findById(id)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    /**
     * 根据支付方式代码查询
     */
    @GetMapping("/code/{paymentCode}")
    public Mono<ResponseEntity<FinPaymentMethodDto>> findByPaymentCode(@PathVariable String paymentCode) {
        return paymentMethodService.findByPaymentCode(paymentCode)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    /**
     * 查询所有启用的支付方式
     */
    @GetMapping
    public Mono<ResponseEntity<List<FinPaymentMethodDto>>> findAllActive() {
        return paymentMethodService.findAllActive()
                .map(ResponseEntity::ok);
    }

    /**
     * 根据平台代码查询启用的支付方式
     */
    @GetMapping("/platform/{platformCode}")
    public Mono<ResponseEntity<List<FinPaymentMethodDto>>> findByPlatformCode(@PathVariable String platformCode) {
        return paymentMethodService.findActiveByPlatformCode(platformCode)
                .map(ResponseEntity::ok);
    }

    /**
     * 删除支付方式
     */
    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> deleteById(@PathVariable Long id) {
        return paymentMethodService.deleteById(id)
                .then(Mono.just(ResponseEntity.ok().<Void>build()));
    }

    /**
     * 绑定支付方式与账户
     */
    @PostMapping("/{paymentMethodId}/bind/{accountId}")
    public Mono<ResponseEntity<FinPaymentBindingDto>> bindAccount(
            @PathVariable Long paymentMethodId,
            @PathVariable Long accountId,
            @RequestParam(required = false, defaultValue = "0") Integer priority) {
        return paymentMethodService.bindAccount(paymentMethodId, accountId, priority)
                .map(ResponseEntity::ok);
    }

    /**
     * 解绑支付方式与账户
     */
    @DeleteMapping("/{paymentMethodId}/unbind/{accountId}")
    public Mono<ResponseEntity<Void>> unbindAccount(
            @PathVariable Long paymentMethodId,
            @PathVariable Long accountId) {
        return paymentMethodService.unbindAccount(paymentMethodId, accountId)
                .then(Mono.just(ResponseEntity.ok().<Void>build()));
    }

    /**
     * 查询支付方式绑定的账户
     */
    @GetMapping("/{paymentMethodId}/bindings")
    public Mono<ResponseEntity<List<FinPaymentBindingDto>>> findBindings(@PathVariable Long paymentMethodId) {
        return paymentMethodService.findBindingsByPaymentMethodId(paymentMethodId)
                .map(ResponseEntity::ok);
    }
}
