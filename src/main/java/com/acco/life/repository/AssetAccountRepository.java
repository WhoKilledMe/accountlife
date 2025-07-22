package com.acco.life.repository;

import com.acco.life.entity.AssetAccount;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

public interface AssetAccountRepository extends ReactiveCrudRepository<AssetAccount, Integer> {
}
