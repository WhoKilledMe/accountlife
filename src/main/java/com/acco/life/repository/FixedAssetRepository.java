package com.acco.life.repository;

import com.acco.life.entity.FixedAsset;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

public interface FixedAssetRepository extends ReactiveCrudRepository<FixedAsset, Integer> {
}
