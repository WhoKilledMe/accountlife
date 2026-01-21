package com.acco.life.service.fin.impl;

import com.acco.life.dto.StatisticsDto;
import com.acco.life.entity.fin.FinAccount;
import com.acco.life.entity.fin.FinTransaction;
import com.acco.life.enums.fin.BizType;
import com.acco.life.repository.fin.*;
import com.acco.life.service.fin.FinStatisticsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 新版统计服务实现（基于 fin_* 表结构）
 *
 * @author wensen.zhang
 * @version V2.0.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class FinStatisticsServiceImpl implements FinStatisticsService {

    private final FinAccountRepository accountRepository;
    private final FinTransactionRepository transactionRepository;
    private final FinStatementFileRepository statementFileRepository;
    private final FinStatementRepository statementRepository;
    private final FinPaymentRouteRepository paymentRouteRepository;

    @Override
    public Mono<StatisticsDto> getUserTotalAssets(Long userId) {
        return accountRepository.findByUserId(userId)
                .filter(account -> "ACTIVE".equals(account.getStatus()))
                .map(account -> account.getBalance() != null ? account.getBalance() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .map(total -> StatisticsDto.builder()
                        .userId(userId)
                        .type("totalAssets")
                        .value(total)
                        .unit("CNY")
                        .build());
    }

    @Override
    public Mono<StatisticsDto> getMonthlyStatistics(Long userId, LocalDate month) {
        LocalDateTime startTime = month.withDayOfMonth(1).atStartOfDay();
        LocalDateTime endTime = month.withDayOfMonth(month.lengthOfMonth()).atTime(LocalTime.MAX);
        
        return transactionRepository.findByUserIdAndTimeRange(userId, startTime, endTime)
                .collectList()
                .map(transactions -> {
                    BigDecimal income = transactions.stream()
                            .filter(t -> BizType.INCOME.getCode().equals(t.getBizType()))
                            .map(FinTransaction::getAmount)
                            .reduce(BigDecimal.ZERO, BigDecimal::add);
                    
                    BigDecimal expense = transactions.stream()
                            .filter(t -> BizType.PAY.getCode().equals(t.getBizType()))
                            .map(FinTransaction::getAmount)
                            .reduce(BigDecimal.ZERO, BigDecimal::add);
                    
                    return StatisticsDto.builder()
                            .userId(userId)
                            .type("monthly")
                            .date(month)
                            .income(income)
                            .expense(expense)
                            .build();
                });
    }

    @Override
    public Mono<StatisticsDto> getYearlyStatistics(Long userId, int year) {
        LocalDateTime startTime = LocalDate.of(year, 1, 1).atStartOfDay();
        LocalDateTime endTime = LocalDate.of(year, 12, 31).atTime(LocalTime.MAX);
        
        return transactionRepository.findByUserIdAndTimeRange(userId, startTime, endTime)
                .collectList()
                .map(transactions -> {
                    BigDecimal income = transactions.stream()
                            .filter(t -> BizType.INCOME.getCode().equals(t.getBizType()))
                            .map(FinTransaction::getAmount)
                            .reduce(BigDecimal.ZERO, BigDecimal::add);
                    
                    BigDecimal expense = transactions.stream()
                            .filter(t -> BizType.PAY.getCode().equals(t.getBizType()))
                            .map(FinTransaction::getAmount)
                            .reduce(BigDecimal.ZERO, BigDecimal::add);
                    
                    return StatisticsDto.builder()
                            .userId(userId)
                            .type("yearly")
                            .date(LocalDate.of(year, 1, 1))
                            .income(income)
                            .expense(expense)
                            .build();
                });
    }

    @Override
    public Mono<List<StatisticsDto>> getCategoryStatistics(Long userId, LocalDate startDate, LocalDate endDate) {
        LocalDateTime startTime = startDate.atStartOfDay();
        LocalDateTime endTime = endDate.atTime(LocalTime.MAX);
        
        return transactionRepository.findByUserIdAndTimeRange(userId, startTime, endTime)
                .collectList()
                .map(transactions -> {
                    Map<Long, StatisticsDto> categoryMap = new HashMap<>();
                    
                    for (FinTransaction t : transactions) {
                        if (t.getCategoryId() == null) continue;
                        
                        Long categoryId = t.getCategoryId();
                        StatisticsDto dto = categoryMap.getOrDefault(categoryId, 
                                StatisticsDto.builder()
                                        .userId(userId)
                                        .categoryId(categoryId)
                                        .income(BigDecimal.ZERO)
                                        .expense(BigDecimal.ZERO)
                                        .build());
                        
                        if (BizType.INCOME.getCode().equals(t.getBizType())) {
                            dto.setIncome(dto.getIncome().add(t.getAmount()));
                        } else if (BizType.PAY.getCode().equals(t.getBizType())) {
                            dto.setExpense(dto.getExpense().add(t.getAmount()));
                        }
                        
                        categoryMap.put(categoryId, dto);
                    }
                    
                    return List.copyOf(categoryMap.values());
                });
    }

    @Override
    public Mono<List<StatisticsDto>> getAccountBalanceStatistics(Long userId) {
        return accountRepository.findByUserId(userId)
                .filter(account -> "ACTIVE".equals(account.getStatus()))
                .map(account -> StatisticsDto.builder()
                        .userId(userId)
                        .accountId(account.getId())
                        .accountName(account.getAccountName())
                        .balance(account.getBalance() != null ? account.getBalance() : BigDecimal.ZERO)
                        .type("accountBalance")
                        .build())
                .collectList();
    }

    @Override
    public Mono<Map<String, Object>> getTrendStatistics(Long userId, LocalDate startDate, LocalDate endDate) {
        LocalDateTime startTime = startDate.atStartOfDay();
        LocalDateTime endTime = endDate.atTime(LocalTime.MAX);
        
        return transactionRepository.findByUserIdAndTimeRange(userId, startTime, endTime)
                .collectList()
                .map(transactions -> {
                    Map<LocalDate, BigDecimal> incomeTrend = new HashMap<>();
                    Map<LocalDate, BigDecimal> expenseTrend = new HashMap<>();
                    
                    for (FinTransaction t : transactions) {
                        LocalDate date = t.getTradeTime().toLocalDate();
                        
                        if (BizType.INCOME.getCode().equals(t.getBizType())) {
                            incomeTrend.merge(date, t.getAmount(), BigDecimal::add);
                        } else if (BizType.PAY.getCode().equals(t.getBizType())) {
                            expenseTrend.merge(date, t.getAmount(), BigDecimal::add);
                        }
                    }
                    
                    Map<String, Object> result = new HashMap<>();
                    result.put("incomeTrend", incomeTrend);
                    result.put("expenseTrend", expenseTrend);
                    return result;
                });
    }

    @Override
    public Mono<List<StatisticsDto>> getPaymentMethodStatistics(Long userId, LocalDate startDate, LocalDate endDate) {
        LocalDateTime startTime = startDate.atStartOfDay();
        LocalDateTime endTime = endDate.atTime(LocalTime.MAX);
        
        return transactionRepository.findByUserIdAndTimeRange(userId, startTime, endTime)
                .flatMap(transaction -> paymentRouteRepository.findByTransactionId(transaction.getId())
                        .map(route -> Map.entry(route.getPaymentMethodId(), route.getAmount())))
                .collectList()
                .map(entries -> {
                    Map<Long, BigDecimal> paymentMethodTotals = entries.stream()
                            .collect(Collectors.groupingBy(
                                    Map.Entry::getKey,
                                    Collectors.reducing(BigDecimal.ZERO, Map.Entry::getValue, BigDecimal::add)
                            ));
                    
                    return paymentMethodTotals.entrySet().stream()
                            .map(entry -> StatisticsDto.builder()
                                    .userId(userId)
                                    .type("paymentMethod")
                                    .value(entry.getValue())
                                    .build())
                            .collect(Collectors.toList());
                });
    }

    @Override
    public Mono<List<StatisticsDto>> getPlatformStatistics(Long userId, LocalDate startDate, LocalDate endDate) {
        LocalDateTime startTime = startDate.atStartOfDay();
        LocalDateTime endTime = endDate.atTime(LocalTime.MAX);
        
        return transactionRepository.findByUserIdAndTimeRange(userId, startTime, endTime)
                .collectList()
                .map(transactions -> {
                    Map<String, BigDecimal> platformTotals = transactions.stream()
                            .filter(t -> t.getPlatformCode() != null)
                            .collect(Collectors.groupingBy(
                                    FinTransaction::getPlatformCode,
                                    Collectors.reducing(BigDecimal.ZERO, FinTransaction::getAmount, BigDecimal::add)
                            ));
                    
                    return platformTotals.entrySet().stream()
                            .map(entry -> StatisticsDto.builder()
                                    .userId(userId)
                                    .type("platform")
                                    .unit(entry.getKey())
                                    .value(entry.getValue())
                                    .build())
                            .collect(Collectors.toList());
                });
    }

    @Override
    public Mono<StatisticsDto> getStatementImportStatistics(Long userId) {
        Mono<Long> totalFilesMono = statementFileRepository.countSearch(userId, null, null);
        Mono<Long> completedFilesMono = statementFileRepository.countSearch(userId, null, "COMPLETED");
        Mono<Long> totalStatementsMono = statementRepository.countSearch(userId, null, null, null);
        
        return Mono.zip(totalFilesMono, completedFilesMono, totalStatementsMono)
                .map(tuple -> StatisticsDto.builder()
                        .userId(userId)
                        .type("statementImport")
                        .value(new BigDecimal(tuple.getT1())) // 总文件数
                        .target(new BigDecimal(tuple.getT2())) // 完成文件数
                        .balance(new BigDecimal(tuple.getT3())) // 总账单行数
                        .build());
    }
}
