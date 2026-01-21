package com.acco.life.service.fin.impl;

import com.acco.life.common.PageResponse;
import com.acco.life.dto.fin.FinPaymentRouteDto;
import com.acco.life.dto.fin.FinTransactionDto;
import com.acco.life.entity.fin.FinPaymentRoute;
import com.acco.life.entity.fin.FinTransaction;
import com.acco.life.enums.fin.BizType;
import com.acco.life.enums.fin.TransactionStatus;
import com.acco.life.mapper.fin.FinPaymentRouteMapper;
import com.acco.life.mapper.fin.FinTransactionMapper;
import com.acco.life.repository.fin.FinPaymentRouteRepository;
import com.acco.life.repository.fin.FinTransactionRepository;
import com.acco.life.service.fin.FinTransactionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

/**
 * 交易中枢服务实现
 *
 * @author wensen.zhang
 * @version V2.0.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class FinTransactionServiceImpl implements FinTransactionService {

    private final FinTransactionRepository transactionRepository;
    private final FinPaymentRouteRepository paymentRouteRepository;
    private final FinTransactionMapper transactionMapper;
    private final FinPaymentRouteMapper paymentRouteMapper;

    @Override
    public Mono<FinTransactionDto> create(FinTransactionDto dto) {
        return generateTransactionNo()
                .flatMap(txNo -> {
                    FinTransaction entity = transactionMapper.toEntity(dto);
                    entity.setTransactionNo(txNo);
                    entity.setIsDeleted(0);
                    if (entity.getStatus() == null) {
                        entity.setStatus(TransactionStatus.INIT.getCode());
                    }
                    if (entity.getCurrency() == null) {
                        entity.setCurrency("CNY");
                    }
                    return transactionRepository.save(entity);
                })
                .map(this::enrichDto);
    }

    @Override
    public Mono<FinTransactionDto> update(FinTransactionDto dto) {
        return transactionRepository.findById(dto.getId())
                .flatMap(existing -> {
                    transactionMapper.updateEntityFromDto(dto, existing);
                    return transactionRepository.save(existing);
                })
                .map(this::enrichDto);
    }

    @Override
    public Mono<FinTransactionDto> findById(Long id) {
        return transactionRepository.findById(id)
                .map(this::enrichDto)
                .flatMap(this::enrichWithPaymentRoutes);
    }

    @Override
    public Mono<FinTransactionDto> findByTransactionNo(String transactionNo) {
        return transactionRepository.findByTransactionNo(transactionNo)
                .map(this::enrichDto)
                .flatMap(this::enrichWithPaymentRoutes);
    }

    @Override
    public Mono<List<FinTransactionDto>> findByUserIdAndTimeRange(Long userId, LocalDateTime startTime, LocalDateTime endTime) {
        return transactionRepository.findByUserIdAndTimeRange(userId, startTime, endTime)
                .map(this::enrichDto)
                .collectList();
    }

    @Override
    public Mono<PageResponse<FinTransactionDto>> page(Long userId, String bizType, String status, String platformCode, Long categoryId, int page, int size) {
        int currentPage = Math.max(page, 0);
        int pageSize = Math.max(size, 1);
        long offset = (long) currentPage * pageSize;

        Mono<List<FinTransactionDto>> dataMono = transactionRepository.search(userId, bizType, status, platformCode, categoryId, pageSize, offset)
                .map(this::enrichDto)
                .collectList();

        Mono<Long> countMono = transactionRepository.countSearch(userId, bizType, status, platformCode, categoryId);

        return Mono.zip(dataMono, countMono)
                .map(tuple -> PageResponse.of(tuple.getT1(), currentPage, pageSize, tuple.getT2()));
    }

    @Override
    public Mono<Void> deleteById(Long id) {
        return transactionRepository.findById(id)
                .flatMap(entity -> {
                    entity.setIsDeleted(1);
                    return transactionRepository.save(entity);
                })
                .then();
    }

    @Override
    public Mono<String> generateTransactionNo() {
        String dateStr = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        String uuid = UUID.randomUUID().toString().replace("-", "").substring(0, 8).toUpperCase();
        return Mono.just("TX" + dateStr + uuid);
    }

    @Override
    public Mono<FinTransactionDto> confirmTransaction(Long transactionId) {
        return transactionRepository.findById(transactionId)
                .flatMap(entity -> {
                    entity.setStatus(TransactionStatus.CONFIRMED.getCode());
                    return transactionRepository.save(entity);
                })
                .map(this::enrichDto);
    }

    @Override
    public Mono<FinTransactionDto> cancelTransaction(Long transactionId) {
        return transactionRepository.findById(transactionId)
                .flatMap(entity -> {
                    entity.setStatus(TransactionStatus.CANCELLED.getCode());
                    return transactionRepository.save(entity);
                })
                .map(this::enrichDto);
    }

    @Override
    public Mono<FinPaymentRouteDto> addPaymentRoute(FinPaymentRouteDto routeDto) {
        FinPaymentRoute entity = paymentRouteMapper.toEntity(routeDto);
        return paymentRouteRepository.save(entity)
                .map(paymentRouteMapper::toDto);
    }

    @Override
    public Mono<List<FinPaymentRouteDto>> findPaymentRoutes(Long transactionId) {
        return paymentRouteRepository.findByTransactionIdOrderByRouteOrder(transactionId)
                .map(paymentRouteMapper::toDto)
                .collectList();
    }

    private FinTransactionDto enrichDto(FinTransaction entity) {
        FinTransactionDto dto = transactionMapper.toDto(entity);
        
        BizType bizType = BizType.fromCode(entity.getBizType());
        if (bizType != null) {
            dto.setBizTypeName(bizType.getName());
        }
        
        TransactionStatus status = TransactionStatus.fromCode(entity.getStatus());
        if (status != null) {
            dto.setStatusName(status.getName());
        }
        
        return dto;
    }

    private Mono<FinTransactionDto> enrichWithPaymentRoutes(FinTransactionDto dto) {
        return findPaymentRoutes(dto.getId())
                .map(routes -> {
                    dto.setPaymentRoutes(routes);
                    return dto;
                });
    }
}
