package com.acco.life.repository;

import com.acco.life.entity.PlatformTransaction;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

public interface PlatformTransactionRepository extends ReactiveCrudRepository<PlatformTransaction, Integer> {
}
