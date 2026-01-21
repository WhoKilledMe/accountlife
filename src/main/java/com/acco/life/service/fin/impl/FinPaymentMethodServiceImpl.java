package com.acco.life.service.fin.impl;

import com.acco.life.dto.fin.FinPaymentBindingDto;
import com.acco.life.dto.fin.FinPaymentMethodDto;
import com.acco.life.entity.fin.FinPaymentBinding;
import com.acco.life.entity.fin.FinPaymentMethod;
import com.acco.life.enums.fin.PaymentType;
import com.acco.life.mapper.fin.FinPaymentBindingMapper;
import com.acco.life.mapper.fin.FinPaymentMethodMapper;
import com.acco.life.repository.fin.FinPaymentBindingRepository;
import com.acco.life.repository.fin.FinPaymentMethodRepository;
import com.acco.life.service.fin.FinPaymentMethodService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * 支付方式管理服务实现
 *
 * @author wensen.zhang
 * @version V2.0.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class FinPaymentMethodServiceImpl implements FinPaymentMethodService {

    private final FinPaymentMethodRepository paymentMethodRepository;
    private final FinPaymentBindingRepository paymentBindingRepository;
    private final FinPaymentMethodMapper paymentMethodMapper;
    private final FinPaymentBindingMapper paymentBindingMapper;

    @Override
    public Mono<FinPaymentMethodDto> create(FinPaymentMethodDto dto) {
        FinPaymentMethod entity = paymentMethodMapper.toEntity(dto);
        if (entity.getStatus() == null) {
            entity.setStatus("ACTIVE");
        }
        return paymentMethodRepository.save(entity)
                .map(this::enrichDto);
    }

    @Override
    public Mono<FinPaymentMethodDto> update(FinPaymentMethodDto dto) {
        return paymentMethodRepository.findById(dto.getId())
                .flatMap(existing -> {
                    paymentMethodMapper.updateEntityFromDto(dto, existing);
                    return paymentMethodRepository.save(existing);
                })
                .map(this::enrichDto);
    }

    @Override
    public Mono<FinPaymentMethodDto> findById(Long id) {
        return paymentMethodRepository.findById(id)
                .map(this::enrichDto);
    }

    @Override
    public Mono<FinPaymentMethodDto> findByPaymentCode(String paymentCode) {
        return paymentMethodRepository.findByPaymentCode(paymentCode)
                .map(this::enrichDto);
    }

    @Override
    public Mono<List<FinPaymentMethodDto>> findAllActive() {
        return paymentMethodRepository.findAllActive()
                .map(this::enrichDto)
                .collectList();
    }

    @Override
    public Mono<List<FinPaymentMethodDto>> findActiveByPlatformCode(String platformCode) {
        return paymentMethodRepository.findActiveByPlatformCode(platformCode)
                .map(this::enrichDto)
                .collectList();
    }

    @Override
    public Mono<Void> deleteById(Long id) {
        return paymentMethodRepository.findById(id)
                .flatMap(entity -> {
                    entity.setStatus("INACTIVE");
                    return paymentMethodRepository.save(entity);
                })
                .then();
    }

    @Override
    public Mono<FinPaymentBindingDto> bindAccount(Long paymentMethodId, Long accountId, Integer priority) {
        return paymentBindingRepository.findByPaymentMethodIdAndAccountId(paymentMethodId, accountId)
                .switchIfEmpty(Mono.defer(() -> {
                    FinPaymentBinding binding = new FinPaymentBinding();
                    binding.setPaymentMethodId(paymentMethodId);
                    binding.setAccountId(accountId);
                    binding.setPriority(priority != null ? priority : 0);
                    binding.setEnabled(true);
                    return paymentBindingRepository.save(binding);
                }))
                .map(paymentBindingMapper::toDto);
    }

    @Override
    public Mono<Void> unbindAccount(Long paymentMethodId, Long accountId) {
        return paymentBindingRepository.findByPaymentMethodIdAndAccountId(paymentMethodId, accountId)
                .flatMap(binding -> {
                    binding.setEnabled(false);
                    return paymentBindingRepository.save(binding);
                })
                .then();
    }

    @Override
    public Mono<List<FinPaymentBindingDto>> findBindingsByPaymentMethodId(Long paymentMethodId) {
        return paymentBindingRepository.findEnabledByPaymentMethodIdOrderByPriority(paymentMethodId)
                .map(paymentBindingMapper::toDto)
                .collectList();
    }

    @Override
    public Mono<List<FinPaymentBindingDto>> findBindingsByAccountId(Long accountId) {
        return paymentBindingRepository.findEnabledByAccountId(accountId)
                .map(paymentBindingMapper::toDto)
                .collectList();
    }

    private FinPaymentMethodDto enrichDto(FinPaymentMethod entity) {
        FinPaymentMethodDto dto = paymentMethodMapper.toDto(entity);
        PaymentType paymentType = PaymentType.fromCode(entity.getPaymentType());
        if (paymentType != null) {
            dto.setPaymentTypeName(paymentType.getName());
        }
        return dto;
    }
}
