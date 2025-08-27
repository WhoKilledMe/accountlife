package com.acco.life.repository;

import com.acco.life.entity.Budget;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

public interface BudgetRepository extends ReactiveCrudRepository<Budget, Integer> {
    Flux<Budget> findByUserId(Integer userId);

    Flux<Budget> findByUserIdAndStartDateBetween(Integer userId, java.time.LocalDate start, java.time.LocalDate end);

    Flux<Budget> findByUserIdAndType(Integer userId, Integer type);

    Flux<Budget> findByUserIdAndCategoryId(Integer userId, Integer categoryId);
}