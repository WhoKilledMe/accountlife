package com.acco.life.repository;

import com.acco.life.entity.InvestmentAsset;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

public interface InvestmentAssetRepository extends ReactiveCrudRepository<InvestmentAsset, Integer> {
}
