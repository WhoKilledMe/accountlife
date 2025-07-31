package com.acco.life.repository;

import com.acco.life.entity.AssetAccount;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

/**
 * description: 资产账户仓库接口，提供资产账户的数据库操作
 *
 * @date: 2025-07-24 17:54:23
 * @author wensen.zhang
 * @version V1.0.0
 */
public interface AssetAccountRepository extends ReactiveCrudRepository<AssetAccount, Integer> {
    
    /**
     * 根据用户ID和账户名称查询资产账户
     * 
     * @param userId 用户ID
     * @param name 账户名称
     * @return 资产账户Mono对象
     */
    Mono<AssetAccount> findByUserIdAndName(Integer userId, String name);
}