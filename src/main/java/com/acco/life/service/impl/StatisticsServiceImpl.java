package com.acco.life.service.impl;

import com.acco.life.dto.StatisticsDto;
import com.acco.life.entity.AccountTransaction;
import com.acco.life.entity.AssetAccount;
import com.acco.life.entity.FixedAsset;
import com.acco.life.mapper.StatisticsMapper;
import com.acco.life.repository.*;
import com.acco.life.service.StatisticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class StatisticsServiceImpl implements StatisticsService {
    private final AccountTransactionRepository transactionRepository;
    private final AssetAccountRepository assetAccountRepository;
    private final InvestmentAssetRepository investmentAssetRepository;
    private final FixedAssetRepository fixedAssetRepository;
    private final TransactionCategoryRepository categoryRepository;
    private final BudgetRepository budgetRepository;
    private final StatisticsMapper statisticsMapper;

    @Override
    public Mono<StatisticsDto> getUserTotalAssets(Long userId) {
        Mono<BigDecimal> assetSum = assetAccountRepository.findAll()
                .filter(a -> a.getUserId().equals(userId))
                .map(AssetAccount::getBalance)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        Mono<BigDecimal> investSum = investmentAssetRepository.findAll()
                .filter(a -> a.getUserId().equals(userId))
                .map(a -> a.getMarketPrice() != null && a.getQuantity() != null ? a.getMarketPrice().multiply(a.getQuantity()) : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        Mono<BigDecimal> fixedSum = fixedAssetRepository.findAll()
                .filter(a -> a.getUserId().equals(userId))
                .map(FixedAsset::getValue)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        return Mono.zip(assetSum, investSum, fixedSum)
                .map(tuple -> StatisticsDto.builder()
                        .userId(userId)
                        .type("totalAssets")
                        .value(tuple.getT1().add(tuple.getT2()).add(tuple.getT3()))
                        .unit("CNY")
                        .build()
                );
    }

    @Override
    public Mono<StatisticsDto> getMonthlyStatistics(Long userId, LocalDate month) {
        LocalDate start = month.withDayOfMonth(1);
        LocalDate end = month.withDayOfMonth(month.lengthOfMonth());
        return transactionRepository.findAll()
                .filter(t -> t.getUserId().equals(userId)
                        && !t.getTransactionTime().toLocalDate().isBefore(start)
                        && !t.getTransactionTime().toLocalDate().isAfter(end))
                .collectList()
                .map(list -> {
                    BigDecimal income = list.stream().filter(t -> t.getType() == 1).map(AccountTransaction::getAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
                    BigDecimal expense = list.stream().filter(t -> t.getType() == 2).map(AccountTransaction::getAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
                    return StatisticsDto.builder()
                            .userId(userId)
                            .type("monthly")
                            .date(start)
                            .income(income)
                            .expense(expense)
                            .build();
                });
    }

    @Override
    public Mono<StatisticsDto> getYearlyStatistics(Long userId, int year) {
        LocalDate start = LocalDate.of(year, 1, 1);
        LocalDate end = LocalDate.of(year, 12, 31);
        return transactionRepository.findAll()
                .filter(t -> t.getUserId().equals(userId)
                        && !t.getTransactionTime().toLocalDate().isBefore(start)
                        && !t.getTransactionTime().toLocalDate().isAfter(end))
                .collectList()
                .map(list -> {
                    BigDecimal income = list.stream().filter(t -> t.getType() == 1).map(AccountTransaction::getAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
                    BigDecimal expense = list.stream().filter(t -> t.getType() == 2).map(AccountTransaction::getAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
                    return StatisticsDto.builder()
                            .userId(userId)
                            .type("yearly")
                            .date(start)
                            .income(income)
                            .expense(expense)
                            .build();
                });
    }

    @Override
    public Mono<List<StatisticsDto>> getCategoryStatistics(Long userId, LocalDate startDate, LocalDate endDate) {
        return transactionRepository.findAll()
                .filter(t -> t.getUserId().equals(userId)
                        && !t.getTransactionTime().toLocalDate().isBefore(startDate)
                        && !t.getTransactionTime().toLocalDate().isAfter(endDate))
                .collectList()
                .map(list -> {
                    Map<Long, StatisticsDto> map = new HashMap<>();
                    for (AccountTransaction t : list) {
                        long categoryId = t.getCategoryId();
                        StatisticsDto dto = map.getOrDefault(categoryId, StatisticsDto.builder()
                                .userId(userId)
                                .categoryId(categoryId)
                                .income(BigDecimal.ZERO)
                                .expense(BigDecimal.ZERO)
                                .build());
                        if (t.getType() == 1) {
                            dto.setIncome(dto.getIncome().add(t.getAmount()));
                        } else if (t.getType() == 2) {
                            dto.setExpense(dto.getExpense().add(t.getAmount()));
                        }
                        map.put(categoryId, dto);
                    }
                    return List.copyOf(map.values());
                });
    }

    @Override
    public Mono<List<StatisticsDto>> getAccountBalanceStatistics(Long userId) {
        return assetAccountRepository.findAll()
                .filter(a -> a.getUserId().equals(userId))
                .map(a -> StatisticsDto.builder()
                        .userId(userId)
                        .accountId(a.getId())
                        .accountName(a.getName())
                        .balance(a.getBalance())
                        .type("accountBalance")
                        .build())
                .collectList();
    }

    @Override
    public Mono<List<StatisticsDto>> getInvestmentStatistics(Long userId) {
        return investmentAssetRepository.findAll()
                .filter(a -> a.getUserId().equals(userId))
                .map(a -> StatisticsDto.builder()
                        .userId(userId)
                        .accountId(a.getId())
                        .accountName(a.getName())
                        .balance(a.getMarketPrice() != null && a.getQuantity() != null ? a.getMarketPrice().multiply(a.getQuantity()) : BigDecimal.ZERO)
                        .type("investment")
                        .build())
                .collectList();
    }

    @Override
    public Mono<List<StatisticsDto>> getFixedAssetStatistics(Long userId) {
        return fixedAssetRepository.findAll()
                .filter(a -> a.getUserId().equals(userId))
                .map(a -> StatisticsDto.builder()
                        .userId(userId)
                        .accountId(a.getId())
                        .accountName(a.getName())
                        .balance(a.getValue())
                        .type("fixedAsset")
                        .build())
                .collectList();
    }

    @Override
    public Mono<Map<String, Object>> getTrendStatistics(Long userId, LocalDate startDate, LocalDate endDate) {
        // 简单实现：返回每日收支趋势
        return transactionRepository.findAll()
                .filter(t -> t.getUserId().equals(userId)
                        && !t.getTransactionTime().toLocalDate().isBefore(startDate)
                        && !t.getTransactionTime().toLocalDate().isAfter(endDate))
                .collectList()
                .map(list -> {
                    Map<LocalDate, BigDecimal> incomeTrend = new HashMap<>();
                    Map<LocalDate, BigDecimal> expenseTrend = new HashMap<>();
                    for (AccountTransaction t : list) {
                        LocalDate date = t.getTransactionTime().toLocalDate();
                        if (t.getType() == 1) {
                            incomeTrend.put(date, incomeTrend.getOrDefault(date, BigDecimal.ZERO).add(t.getAmount()));
                        } else if (t.getType() == 2) {
                            expenseTrend.put(date, expenseTrend.getOrDefault(date, BigDecimal.ZERO).add(t.getAmount()));
                        }
                    }
                    Map<String, Object> result = new HashMap<>();
                    result.put("incomeTrend", incomeTrend);
                    result.put("expenseTrend", expenseTrend);
                    return result;
                });
    }

    @Override
    public Mono<StatisticsDto> getBudgetExecution(Long userId, LocalDate month) {
        LocalDate start = month.withDayOfMonth(1);
        LocalDate end = month.withDayOfMonth(month.lengthOfMonth());
        return budgetRepository.findByUserIdAndStartDateBetween(userId, start, end)
                .collectList()
                .map(list -> {
                    BigDecimal total = list.stream().map(b -> b.getAmount() == null ? BigDecimal.ZERO : b.getAmount()).reduce(BigDecimal.ZERO, BigDecimal::add);
                    BigDecimal used = list.stream().map(b -> b.getUsedAmount() == null ? BigDecimal.ZERO : b.getUsedAmount()).reduce(BigDecimal.ZERO, BigDecimal::add);
                    BigDecimal completionRate = total.compareTo(BigDecimal.ZERO) > 0 ? used.divide(total, 4, RoundingMode.HALF_UP) : BigDecimal.ZERO;
                    return StatisticsDto.builder()
                            .userId(userId)
                            .type("budgetExecution")
                            .date(start)
                            .target(total)
                            .value(used)
                            .completionRate(completionRate)
                            .build();
                });
    }
}