package com.acco.life.service.fin.impl;

import com.acco.life.common.PageResponse;
import com.acco.life.dto.fin.FinAccountFlowDto;
import com.acco.life.entity.fin.FinAccountFlow;
import com.acco.life.enums.fin.BizType;
import com.acco.life.enums.fin.FlowDirection;
import com.acco.life.mapper.fin.FinAccountFlowMapper;
import com.acco.life.repository.fin.FinAccountFlowRepository;
import com.acco.life.repository.fin.FinAccountRepository;
import com.acco.life.service.fin.FinAccountFlowService;
import com.acco.life.service.fin.FinAccountService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 会计流水服务实现
 *
 * @author wensen.zhang
 * @version V2.0.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class FinAccountFlowServiceImpl implements FinAccountFlowService {

    private final FinAccountFlowRepository accountFlowRepository;
    private final FinAccountRepository accountRepository;
    private final FinAccountFlowMapper accountFlowMapper;
    private final FinAccountService accountService;
    
    private final AtomicLong journalIdGenerator = new AtomicLong(System.currentTimeMillis());

    @Override
    public Mono<FinAccountFlowDto> createFlow(FinAccountFlowDto dto) {
        return generateFlowNo()
                .flatMap(flowNo -> {
                    FinAccountFlow entity = accountFlowMapper.toEntity(dto);
                    entity.setFlowNo(flowNo);
                    return accountFlowRepository.save(entity);
                })
                .map(this::enrichDto);
    }

    @Override
    public Mono<List<FinAccountFlowDto>> createDoubleEntry(Long userId, Long debitAccountId, Long creditAccountId,
                                                            BigDecimal amount, Long transactionId, String bizType, String remark) {
        return generateJournalId()
                .flatMap(journalId -> {
                    LocalDateTime now = LocalDateTime.now();
                    
                    // 获取借方账户当前余额
                    Mono<FinAccountFlowDto> debitFlowMono = accountRepository.findById(debitAccountId)
                            .flatMap(debitAccount -> {
                                BigDecimal debitBalanceBefore = debitAccount.getBalance() != null ? debitAccount.getBalance() : BigDecimal.ZERO;
                                BigDecimal debitBalanceAfter = debitBalanceBefore.add(amount);
                                
                                return generateFlowNo().flatMap(flowNo -> {
                                    FinAccountFlow debitFlow = new FinAccountFlow();
                                    debitFlow.setUserId(userId);
                                    debitFlow.setFlowNo(flowNo);
                                    debitFlow.setJournalId(journalId);
                                    debitFlow.setAccountId(debitAccountId);
                                    debitFlow.setTransactionId(transactionId);
                                    debitFlow.setDirection(FlowDirection.DEBIT.getCode());
                                    debitFlow.setAmount(amount);
                                    debitFlow.setBalanceBefore(debitBalanceBefore);
                                    debitFlow.setBalanceAfter(debitBalanceAfter);
                                    debitFlow.setBizType(bizType);
                                    debitFlow.setTradeTime(now);
                                    debitFlow.setRemark(remark);
                                    
                                    return accountFlowRepository.save(debitFlow)
                                            .flatMap(saved -> accountService.updateBalance(debitAccountId, debitBalanceAfter)
                                                    .thenReturn(saved));
                                });
                            })
                            .map(this::enrichDto);
                    
                    // 获取贷方账户当前余额
                    Mono<FinAccountFlowDto> creditFlowMono = accountRepository.findById(creditAccountId)
                            .flatMap(creditAccount -> {
                                BigDecimal creditBalanceBefore = creditAccount.getBalance() != null ? creditAccount.getBalance() : BigDecimal.ZERO;
                                BigDecimal creditBalanceAfter = creditBalanceBefore.subtract(amount);
                                
                                return generateFlowNo().flatMap(flowNo -> {
                                    FinAccountFlow creditFlow = new FinAccountFlow();
                                    creditFlow.setUserId(userId);
                                    creditFlow.setFlowNo(flowNo);
                                    creditFlow.setJournalId(journalId);
                                    creditFlow.setAccountId(creditAccountId);
                                    creditFlow.setTransactionId(transactionId);
                                    creditFlow.setDirection(FlowDirection.CREDIT.getCode());
                                    creditFlow.setAmount(amount);
                                    creditFlow.setBalanceBefore(creditBalanceBefore);
                                    creditFlow.setBalanceAfter(creditBalanceAfter);
                                    creditFlow.setBizType(bizType);
                                    creditFlow.setTradeTime(now);
                                    creditFlow.setRemark(remark);
                                    
                                    return accountFlowRepository.save(creditFlow)
                                            .flatMap(saved -> accountService.updateBalance(creditAccountId, creditBalanceAfter)
                                                    .thenReturn(saved));
                                });
                            })
                            .map(this::enrichDto);
                    
                    return Mono.zip(debitFlowMono, creditFlowMono)
                            .map(tuple -> Arrays.asList(tuple.getT1(), tuple.getT2()));
                });
    }

    @Override
    public Mono<FinAccountFlowDto> findById(Long id) {
        return accountFlowRepository.findById(id)
                .map(this::enrichDto);
    }

    @Override
    public Mono<List<FinAccountFlowDto>> findByAccountId(Long accountId) {
        return accountFlowRepository.findByAccountId(accountId)
                .map(this::enrichDto)
                .collectList();
    }

    @Override
    public Mono<List<FinAccountFlowDto>> findByTransactionId(Long transactionId) {
        return accountFlowRepository.findByTransactionId(transactionId)
                .map(this::enrichDto)
                .collectList();
    }

    @Override
    public Mono<List<FinAccountFlowDto>> findByAccountIdAndTimeRange(Long accountId, LocalDateTime startTime, LocalDateTime endTime) {
        return accountFlowRepository.findByAccountIdAndTimeRange(accountId, startTime, endTime)
                .map(this::enrichDto)
                .collectList();
    }

    @Override
    public Mono<PageResponse<FinAccountFlowDto>> page(Long userId, Long accountId, String direction, String bizType, int page, int size) {
        int currentPage = Math.max(page, 0);
        int pageSize = Math.max(size, 1);
        long offset = (long) currentPage * pageSize;

        Mono<List<FinAccountFlowDto>> dataMono = accountFlowRepository.search(userId, accountId, direction, bizType, pageSize, offset)
                .map(this::enrichDto)
                .collectList();

        Mono<Long> countMono = accountFlowRepository.countSearch(userId, accountId, direction, bizType);

        return Mono.zip(dataMono, countMono)
                .map(tuple -> PageResponse.of(tuple.getT1(), currentPage, pageSize, tuple.getT2()));
    }

    @Override
    public Mono<Boolean> validateJournalBalance(Long journalId) {
        return accountFlowRepository.findByJournalId(journalId)
                .collectList()
                .map(flows -> {
                    BigDecimal debitSum = flows.stream()
                            .filter(f -> FlowDirection.DEBIT.getCode().equals(f.getDirection()))
                            .map(FinAccountFlow::getAmount)
                            .reduce(BigDecimal.ZERO, BigDecimal::add);
                    
                    BigDecimal creditSum = flows.stream()
                            .filter(f -> FlowDirection.CREDIT.getCode().equals(f.getDirection()))
                            .map(FinAccountFlow::getAmount)
                            .reduce(BigDecimal.ZERO, BigDecimal::add);
                    
                    return debitSum.compareTo(creditSum) == 0;
                });
    }

    @Override
    public Mono<BigDecimal> calculateBalanceAt(Long accountId, LocalDateTime dateTime) {
        return accountFlowRepository.findByAccountIdAndTimeRange(accountId, LocalDateTime.MIN, dateTime)
                .collectList()
                .map(flows -> {
                    if (flows.isEmpty()) {
                        return BigDecimal.ZERO;
                    }
                    // 返回最后一条流水的余额
                    return flows.get(flows.size() - 1).getBalanceAfter();
                });
    }

    @Override
    public Mono<String> generateFlowNo() {
        String dateStr = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        String uuid = UUID.randomUUID().toString().replace("-", "").substring(0, 8).toUpperCase();
        return Mono.just("FL" + dateStr + uuid);
    }

    @Override
    public Mono<Long> generateJournalId() {
        return Mono.just(journalIdGenerator.incrementAndGet());
    }

    private FinAccountFlowDto enrichDto(FinAccountFlow entity) {
        FinAccountFlowDto dto = accountFlowMapper.toDto(entity);
        
        FlowDirection direction = FlowDirection.fromCode(entity.getDirection());
        if (direction != null) {
            dto.setDirectionName(direction.getName());
        }
        
        BizType bizType = BizType.fromCode(entity.getBizType());
        if (bizType != null) {
            dto.setBizTypeName(bizType.getName());
        }
        
        return dto;
    }
}
