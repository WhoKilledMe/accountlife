package com.acco.life.repository;

import com.acco.life.entity.AssetAccount;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

/**
 * description: 资产账户仓库接口，提供资产账户的数据库操作
 *
 * @date: 2025-07-24 17:54:23
 * @author wensen.zhang
 * @version V1.0.0
 */
public interface AssetAccountRepository extends ReactiveCrudRepository<AssetAccount, Integer> {
}
