package com.acco.life.service.fin.impl;

import com.acco.life.common.PageResponse;
import com.acco.life.dto.fin.FinClearingFlowDto;
import com.acco.life.entity.fin.FinAccount;
import com.acco.life.entity.fin.FinClearingFlow;
import com.acco.life.mapper.fin.FinClearingFlowMapper;
import com.acco.life.repository.fin.FinAccountRepository;
import com.acco.life.repository.fin.FinClearingFlowRepository;
import com.acco.life.service.fin.FinClearingFlowService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

/**
 * 清算流水服务实现
 *
 * @author wensen.zhang
 * @version V2.0.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class FinClearingFlowServiceImpl implements FinClearingFlowService {

    private final FinClearingFlowRepository clearingFlowRepository;
    private final FinAccountRepository accountRepository;
    private final FinClearingFlowMapper clearingFlowMapper;

    @Override
    public Mono<FinClearingFlowDto> createClearingFlow(Long userId, Long fromAccountId, Long toAccountId,
                                                        Long transactionId, BigDecimal amount, String currency,
                                                        LocalDateTime tradeTime, String remark) {
        return generateFlowNo()
                .flatMap(flowNo -> {
                    FinClearingFlow entity = new FinClearingFlow();
                    entity.setUserId(userId);
                    entity.setFlowNo(flowNo);
                    entity.setFromAccountId(fromAccountId);
                    entity.setToAccountId(toAccountId);
                    entity.setTransactionId(transactionId);
                    entity.setAmount(amount);
                    entity.setCurrency(currency != null ? currency : "CNY");
                    entity.setStatus("SUCCESS"); // 默认已清算成功
                    entity.setTradeTime(tradeTime != null ? tradeTime : LocalDateTime.now());
                    entity.setRemark(remark);

                    return clearingFlowRepository.save(entity)
                            .map(this::enrichDto);
                });
    }

    @Override
    public Mono<FinClearingFlowDto> updateClearingStatus(Long flowId, String status) {
        return clearingFlowRepository.findById(flowId)
                .flatMap(entity -> {
                    entity.setStatus(status);
                    return clearingFlowRepository.save(entity);
                })
                .map(this::enrichDto);
    }

    @Override
    public Mono<FinClearingFlowDto> findById(Long id) {
        return clearingFlowRepository.findById(id)
                .map(this::enrichDto);
    }

    @Override
    public Mono<List<FinClearingFlowDto>> findByTransactionId(Long transactionId) {
        return clearingFlowRepository.findByTransactionId(transactionId)
                .map(this::enrichDto)
                .collectList();
    }

    @Override
    public Mono<List<FinClearingFlowDto>> findByAccountId(Long accountId) {
        return Flux.merge(
                        clearingFlowRepository.findByFromAccountId(accountId),
                        clearingFlowRepository.findByToAccountId(accountId)
                )
                .map(this::enrichDto)
                .collectList();
    }

    @Override
    public Mono<List<FinClearingFlowDto>> findByUserIdAndTimeRange(Long userId, LocalDateTime startTime, LocalDateTime endTime) {
        return clearingFlowRepository.findByUserIdAndTimeRange(userId, startTime, endTime)
                .map(this::enrichDto)
                .collectList();
    }

    @Override
    public Mono<PageResponse<FinClearingFlowDto>> page(Long userId, String status, Long accountId, int page, int size) {
        int currentPage = Math.max(page, 0);
        int pageSize = Math.max(size, 1);
        long offset = (long) currentPage * pageSize;

        Flux<FinClearingFlow> flux;
        if (accountId != null) {
            flux = Flux.merge(
                    clearingFlowRepository.findByFromAccountId(accountId),
                    clearingFlowRepository.findByToAccountId(accountId)
            );
        } else if (status != null) {
            flux = clearingFlowRepository.findByUserIdAndStatus(userId, status);
        } else {
            flux = clearingFlowRepository.findByUserId(userId);
        }

        Mono<List<FinClearingFlowDto>> dataMono = flux
                .skip(offset)
                .take(pageSize)
                .map(this::enrichDto)
                .collectList();

        Mono<Long> countMono = flux.count();

        return Mono.zip(dataMono, countMono)
                .map(tuple -> PageResponse.of(tuple.getT1(), currentPage, pageSize, tuple.getT2()));
    }

    /**
     * 生成清算流水号
     */
    private Mono<String> generateFlowNo() {
        return Mono.fromCallable(() -> {
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
            String uuid = UUID.randomUUID().toString().substring(0, 8).toUpperCase();
            return "CL-" + timestamp + "-" + uuid;
        });
    }

    /**
     * 丰富DTO信息
     */
    private FinClearingFlowDto enrichDto(FinClearingFlow entity) {
        FinClearingFlowDto dto = clearingFlowMapper.toDto(entity);

        // 查询账户名称
        if (entity.getFromAccountId() != null) {
            accountRepository.findById(entity.getFromAccountId())
                    .map(FinAccount::getAccountName)
                    .subscribe(dto::setFromAccountName);
        }

        if (entity.getToAccountId() != null) {
            accountRepository.findById(entity.getToAccountId())
                    .map(FinAccount::getAccountName)
                    .subscribe(dto::setToAccountName);
        }

        // 状态名称
        switch (entity.getStatus()) {
            case "INIT" -> dto.setStatusName("初始化");
            case "CLEARING" -> dto.setStatusName("清算中");
            case "SUCCESS" -> dto.setStatusName("清算成功");
            case "FAILED" -> dto.setStatusName("清算失败");
            default -> dto.setStatusName(entity.getStatus());
        }

        return dto;
    }
}
