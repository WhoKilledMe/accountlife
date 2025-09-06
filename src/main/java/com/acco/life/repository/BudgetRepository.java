package com.acco.life.repository;

import com.acco.life.entity.Budget;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;

public interface BudgetRepository extends ReactiveCrudRepository<Budget, Long> {
    Flux<Budget> findByUserId(Long userId);

    Flux<Budget> findByUserIdAndStartDateBetween(Long userId, java.time.LocalDate start, java.time.LocalDate end);

    Flux<Budget> findByUserIdAndType(Long userId, Integer type);

    Flux<Budget> findByUserIdAndCategoryId(Long userId, Long categoryId);
}