package com.acco.life.repository;

import com.acco.life.entity.CreditWalletStatement;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

public interface CreditWalletStatementRepository extends ReactiveCrudRepository<CreditWalletStatement, Integer> {
}
