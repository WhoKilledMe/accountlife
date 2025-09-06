package com.acco.life.repository;

import com.acco.life.entity.InvestmentAsset;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

/**
 * description: 投资资产仓库接口，提供投资资产的数据库操作
 *
 * @date: 2025-07-24 17:54:23
 * @author wensen.zhang
 * @version V1.0.0
 */
public interface InvestmentAssetRepository extends ReactiveCrudRepository<InvestmentAsset, Long> {
}
