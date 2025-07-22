package com.acco.life.repository;

import com.acco.life.entity.AccountTransaction;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

public interface AccountTransactionRepository extends ReactiveCrudRepository<AccountTransaction, Integer> {
}
