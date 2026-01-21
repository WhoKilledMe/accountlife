package com.acco.life.service.fin.impl;

import com.acco.life.dto.fin.FinStatementAccountMapDto;
import com.acco.life.dto.fin.FinStatementDto;
import com.acco.life.dto.fin.FinTransactionDto;
import com.acco.life.dto.fin.FinTransactionStatementMapDto;
import com.acco.life.entity.fin.*;
import com.acco.life.enums.fin.BizType;
import com.acco.life.enums.fin.FlowDirection;
import com.acco.life.enums.fin.MapType;
import com.acco.life.enums.fin.StatementStatus;
import com.acco.life.enums.fin.TransactionStatus;
import com.acco.life.mapper.fin.FinStatementAccountMapMapper;
import com.acco.life.mapper.fin.FinTransactionMapper;
import com.acco.life.mapper.fin.FinTransactionStatementMapMapper;
import com.acco.life.repository.fin.*;
import com.acco.life.service.fin.FinReconciliationService;
import com.acco.life.service.fin.FinTransactionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 对账归并服务实现
 *
 * @author wensen.zhang
 * @version V2.0.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class FinReconciliationServiceImpl implements FinReconciliationService {

    private final FinStatementRepository statementRepository;
    private final FinAccountRepository accountRepository;
    private final FinTransactionRepository transactionRepository;
    private final FinStatementAccountMapRepository statementAccountMapRepository;
    private final FinTransactionStatementMapRepository transactionStatementMapRepository;
    private final FinStatementAccountMapMapper statementAccountMapMapper;
    private final FinTransactionStatementMapMapper transactionStatementMapMapper;
    private final FinTransactionMapper transactionMapper;
    private final FinTransactionService transactionService;

    @Override
    public Mono<List<FinStatementAccountMapDto>> autoMapStatementToAccount(Long userId) {
        return statementRepository.findPendingReconciliation(userId)
                .flatMap(statement -> tryAutoMapToAccount(statement, userId))
                .collectList();
    }

    @Override
    public Mono<FinStatementAccountMapDto> manualMapStatementToAccount(Long statementId, Long accountId, String remark) {
        return statementAccountMapRepository.findByStatementIdAndAccountId(statementId, accountId)
                .switchIfEmpty(Mono.defer(() -> {
                    FinStatementAccountMap map = new FinStatementAccountMap();
                    map.setStatementId(statementId);
                    map.setAccountId(accountId);
                    map.setMapType(MapType.MANUAL.getCode());
                    map.setConfidence(new BigDecimal("100.00"));
                    map.setMappedBy("USER");
                    map.setMappedAt(LocalDateTime.now());
                    map.setRemark(remark);
                    return statementAccountMapRepository.save(map);
                }))
                .flatMap(map -> {
                    // 更新账单状态
                    return statementRepository.findById(statementId)
                            .flatMap(stmt -> {
                                stmt.setStatus(StatementStatus.MAPPED.getCode());
                                return statementRepository.save(stmt);
                            })
                            .thenReturn(map);
                })
                .map(statementAccountMapMapper::toDto);
    }

    @Override
    public Mono<List<FinTransactionStatementMapDto>> autoMergeStatementToTransaction(Long userId) {
        return statementRepository.findByUserIdAndStatus(userId, StatementStatus.MAPPED.getCode())
                .flatMap(this::tryAutoMergeToTransaction)
                .collectList();
    }

    @Override
    public Mono<FinTransactionStatementMapDto> manualMergeStatementToTransaction(Long statementId, Long transactionId, String mapType) {
        return transactionStatementMapRepository.findByTransactionIdAndStatementId(transactionId, statementId)
                .switchIfEmpty(Mono.defer(() -> {
                    FinTransactionStatementMap map = new FinTransactionStatementMap();
                    map.setTransactionId(transactionId);
                    map.setStatementId(statementId);
                    map.setMapType(mapType != null ? mapType : MapType.ONE_TO_ONE.getCode());
                    map.setConfirmStatus("MANUAL_CONFIRMED");
                    map.setConfidence(new BigDecimal("100.00"));
                    map.setMappedAt(LocalDateTime.now());
                    return transactionStatementMapRepository.save(map);
                }))
                .map(transactionStatementMapMapper::toDto);
    }

    @Override
    public Mono<FinTransactionDto> createTransactionFromStatement(Long statementId) {
        return statementRepository.findById(statementId)
                .flatMap(statement -> {
                    FinTransactionDto txDto = new FinTransactionDto();
                    txDto.setUserId(statement.getUserId());
                    txDto.setBizType(determineBizType(statement));
                    txDto.setTradeTime(statement.getStmtTime());
                    txDto.setAmount(statement.getAmount());
                    txDto.setCurrency("CNY");
                    txDto.setStatus(TransactionStatus.MATCHED.getCode());
                    txDto.setCounterparty(statement.getCounterparty());
                    txDto.setPlatformCode(statement.getPlatformCode());
                    txDto.setRemark(statement.getDescription());
                    
                    return transactionService.create(txDto)
                            .flatMap(tx -> {
                                // 创建映射
                                return manualMergeStatementToTransaction(statementId, tx.getId(), MapType.ONE_TO_ONE.getCode())
                                        .thenReturn(tx);
                            });
                });
    }

    @Override
    public Mono<FinTransactionDto> createTransactionFromStatements(List<Long> statementIds, String bizType) {
        return Flux.fromIterable(statementIds)
                .flatMap(statementRepository::findById)
                .collectList()
                .flatMap(statements -> {
                    if (statements.isEmpty()) {
                        return Mono.error(new IllegalArgumentException("未找到账单记录"));
                    }
                    
                    // 计算总金额
                    BigDecimal totalAmount = statements.stream()
                            .map(FinStatement::getAmount)
                            .reduce(BigDecimal.ZERO, BigDecimal::add);
                    
                    FinStatement first = statements.get(0);
                    
                    FinTransactionDto txDto = new FinTransactionDto();
                    txDto.setUserId(first.getUserId());
                    txDto.setBizType(bizType != null ? bizType : determineBizType(first));
                    txDto.setTradeTime(first.getStmtTime());
                    txDto.setAmount(totalAmount);
                    txDto.setCurrency("CNY");
                    txDto.setStatus(TransactionStatus.MATCHED.getCode());
                    txDto.setCounterparty(first.getCounterparty());
                    txDto.setPlatformCode(first.getPlatformCode());
                    txDto.setRemark("合并自 " + statements.size() + " 条账单");
                    
                    return transactionService.create(txDto)
                            .flatMap(tx -> {
                                // 为每条账单创建映射
                                return Flux.fromIterable(statementIds)
                                        .flatMap(stmtId -> manualMergeStatementToTransaction(stmtId, tx.getId(), MapType.MANY_TO_ONE.getCode()))
                                        .then(Mono.just(tx));
                            });
                });
    }

    @Override
    public Mono<List<FinStatementAccountMapDto>> findAccountMapsByStatementId(Long statementId) {
        return statementAccountMapRepository.findByStatementId(statementId)
                .map(statementAccountMapMapper::toDto)
                .collectList();
    }

    @Override
    public Mono<List<FinTransactionStatementMapDto>> findTransactionMapsByStatementId(Long statementId) {
        return transactionStatementMapRepository.findByStatementId(statementId)
                .map(transactionStatementMapMapper::toDto)
                .collectList();
    }

    @Override
    public Mono<List<FinTransactionStatementMapDto>> findStatementMapsByTransactionId(Long transactionId) {
        return transactionStatementMapRepository.findByTransactionId(transactionId)
                .map(transactionStatementMapMapper::toDto)
                .collectList();
    }

    @Override
    public Mono<Void> cancelStatementAccountMap(Long statementId, Long accountId) {
        return statementAccountMapRepository.findByStatementIdAndAccountId(statementId, accountId)
                .flatMap(map -> statementAccountMapRepository.delete(map))
                .then();
    }

    @Override
    public Mono<Void> cancelStatementTransactionMap(Long statementId, Long transactionId) {
        return transactionStatementMapRepository.findByTransactionIdAndStatementId(transactionId, statementId)
                .flatMap(map -> transactionStatementMapRepository.delete(map))
                .then();
    }

    @Override
    public Mono<ReconciliationSummary> getReconciliationSummary(Long userId) {
        Mono<Long> pendingCount = statementRepository.countSearch(userId, null, null, StatementStatus.NEW.getCode());
        Mono<Long> parsedCount = statementRepository.countSearch(userId, null, null, StatementStatus.PARSED.getCode());
        Mono<Long> mappedCount = statementRepository.countSearch(userId, null, null, StatementStatus.MAPPED.getCode());
        Mono<Long> ignoredCount = statementRepository.countSearch(userId, null, null, StatementStatus.IGNORED.getCode());
        
        return Mono.zip(pendingCount, parsedCount, mappedCount, ignoredCount)
                .map(tuple -> new ReconciliationSummary(
                        tuple.getT1().intValue() + tuple.getT2().intValue(),
                        0, // 需要从映射表统计
                        tuple.getT3().intValue(),
                        tuple.getT4().intValue()
                ));
    }

    /**
     * 尝试自动映射账单到账户
     */
    private Mono<FinStatementAccountMapDto> tryAutoMapToAccount(FinStatement statement, Long userId) {
        // 根据 account_ref 尝试匹配账户
        if (statement.getAccountRef() == null || statement.getAccountRef().isEmpty()) {
            return Mono.empty();
        }
        
        return accountRepository.findByUserIdAndAccountNameLike(userId, "%" + statement.getAccountRef() + "%")
                .next()
                .flatMap(account -> {
                    FinStatementAccountMap map = new FinStatementAccountMap();
                    map.setStatementId(statement.getId());
                    map.setAccountId(account.getId());
                    map.setMapType(MapType.AUTO.getCode());
                    map.setConfidence(new BigDecimal("80.00")); // 自动匹配置信度
                    map.setMappedBy("SYSTEM");
                    map.setMappedAt(LocalDateTime.now());
                    
                    return statementAccountMapRepository.save(map)
                            .flatMap(savedMap -> {
                                statement.setStatus(StatementStatus.PARSED.getCode());
                                return statementRepository.save(statement)
                                        .thenReturn(savedMap);
                            });
                })
                .map(statementAccountMapMapper::toDto);
    }

    /**
     * 尝试自动归并账单到交易
     */
    private Mono<FinTransactionStatementMapDto> tryAutoMergeToTransaction(FinStatement statement) {
        // 简单实现：每条账单创建一笔交易
        return createTransactionFromStatement(statement.getId())
                .flatMap(tx -> transactionStatementMapRepository.findByTransactionIdAndStatementId(tx.getId(), statement.getId()))
                .map(transactionStatementMapMapper::toDto);
    }

    /**
     * 根据账单判断业务类型
     */
    private String determineBizType(FinStatement statement) {
        if (statement.getDirection() == null) {
            return BizType.PAY.getCode();
        }
        
        if (FlowDirection.IN.getCode().equals(statement.getDirection())) {
            return BizType.INCOME.getCode();
        } else {
            return BizType.PAY.getCode();
        }
    }
}
