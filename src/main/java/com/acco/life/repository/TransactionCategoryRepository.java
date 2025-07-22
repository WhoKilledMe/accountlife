package com.acco.life.repository;

import com.acco.life.entity.TransactionCategory;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

public interface TransactionCategoryRepository extends ReactiveCrudRepository<TransactionCategory, Integer> {
}
