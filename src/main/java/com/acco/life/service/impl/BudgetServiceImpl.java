package com.acco.life.service.impl;

import com.acco.life.dto.BudgetDto;
import com.acco.life.mapper.BudgetMapper;
import com.acco.life.repository.BudgetRepository;
import com.acco.life.service.BudgetService;
import com.acco.life.util.UserUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BudgetServiceImpl implements BudgetService {

    private final BudgetRepository budgetRepository;
    private final BudgetMapper budgetMapper;

    @Override
    public Mono<List<BudgetDto>> findByUserId(Long userId) {
        return budgetRepository.findByUserId(userId)
                .map(budgetMapper::toDto)
                .collectList();
    }

    @Override
    public Mono<BudgetDto> findById(Long id) {
        return budgetRepository.findById(id)
                .map(budgetMapper::toDto);
    }

    @Override
    @Transactional
    public Mono<BudgetDto> save(Mono<BudgetDto> budgetDtoMono) {
        return budgetDtoMono
                .map(budgetMapper::toEntity)
                .flatMap(budgetRepository::save)
                .map(budgetMapper::toDto);
    }

    @Override
    public Mono<Void> deleteById(Long id) {
        return budgetRepository.deleteById(id);
    }

    @Override
    public Mono<List<BudgetDto>> findByUserIdAndMonth(Long userId, LocalDate month) {
        LocalDate start = month.withDayOfMonth(1);
        LocalDate end = month.withDayOfMonth(month.lengthOfMonth());
        return budgetRepository.findByUserIdAndStartDateBetween(userId, start, end)
                .map(budgetMapper::toDto)
                .collectList();
    }

    @Override
    public Mono<List<BudgetDto>> findByUserIdAndYear(Long userId, int year) {
        LocalDate start = LocalDate.of(year, 1, 1);
        LocalDate end = LocalDate.of(year, 12, 31);
        return budgetRepository.findByUserIdAndStartDateBetween(userId, start, end)
                .map(budgetMapper::toDto)
                .collectList();
    }

    @Override
    public Mono<List<BudgetDto>> findByUserIdAndCategory(Long userId, Long categoryId) {
        return budgetRepository.findByUserIdAndCategoryId(userId, categoryId)
                .map(budgetMapper::toDto)
                .collectList();
    }

    @Override
    @Transactional
    public Mono<BudgetDto> updateUsedAmount(Long budgetId, BigDecimal usedAmount) {
        return budgetRepository.findById(budgetId)
                .flatMap(budget -> {
                    budget.setUsedAmount(usedAmount);
                    return budgetRepository.save(budget);
                })
                .map(budgetMapper::toDto);
    }

    @Override
    public Mono<BudgetDto> checkBudgetStatus(Long budgetId) {
        return budgetRepository.findById(budgetId)
                .flatMap(budget -> {
                    BigDecimal used = budget.getUsedAmount() == null ? BigDecimal.ZERO : budget.getUsedAmount();
                    BigDecimal total = budget.getAmount() == null ? BigDecimal.ZERO : budget.getAmount();
                    if (total.compareTo(BigDecimal.ZERO) > 0) {
                        BigDecimal rate = used.divide(total, 4, RoundingMode.HALF_UP);
                        if (rate.compareTo(budget.getAlertThreshold()) >= 0) {
                            budget.setStatus(3); // 超支
                        } else if (used.compareTo(total) >= 0) {
                            budget.setStatus(2); // 已完成
                        } else {
                            budget.setStatus(1); // 进行中
                        }
                    }
                    return budgetRepository.save(budget);
                })
                .map(budgetMapper::toDto);
    }

    @Override
    public Mono<List<BudgetDto>> getBudgetAlerts(Long userId) {
        return budgetRepository.findByUserId(userId)
                .filter(budget -> {
                    BigDecimal used = budget.getUsedAmount() == null ? BigDecimal.ZERO : budget.getUsedAmount();
                    BigDecimal total = budget.getAmount() == null ? BigDecimal.ZERO : budget.getAmount();
                    if (total.compareTo(BigDecimal.ZERO) > 0 && budget.getAlertThreshold() != null) {
                        BigDecimal rate = used.divide(total, 4, RoundingMode.HALF_UP);
                        return rate.compareTo(budget.getAlertThreshold()) >= 0;
                    }
                    return false;
                })
                .map(budgetMapper::toDto)
                .collectList();
    }

    @Override
    public Mono<List<BudgetDto>> getCurrentMonthBudgetUsage() {
        LocalDate now = LocalDate.now();
        LocalDate start = now.withDayOfMonth(1);
        LocalDate end = now.withDayOfMonth(now.lengthOfMonth());
        
        return UserUtil.getCurrentUserId()
                .flatMap(userId -> budgetRepository.findByUserIdAndStartDateBetween(userId, start, end)
                        .map(budgetMapper::toDto)
                        .collectList());
    }

    @Override
    public Mono<List<BudgetDto>> getMonthBudgetUsage(LocalDate month) {
        LocalDate start = month.withDayOfMonth(1);
        LocalDate end = month.withDayOfMonth(month.lengthOfMonth());
        
        return UserUtil.getCurrentUserId()
                .flatMap(userId -> budgetRepository.findByUserIdAndStartDateBetween(userId, start, end)
                        .map(budgetMapper::toDto)
                        .collectList());
    }
}