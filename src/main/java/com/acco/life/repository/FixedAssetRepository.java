package com.acco.life.repository;

import com.acco.life.entity.FixedAsset;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

/**
 * description: 固定资产仓库接口，提供固定资产的数据库操作
 *
 * @date: 2025-07-24 17:54:23
 * @author wensen.zhang
 * @version V1.0.0
 */
public interface FixedAssetRepository extends ReactiveCrudRepository<FixedAsset, Integer> {
}
