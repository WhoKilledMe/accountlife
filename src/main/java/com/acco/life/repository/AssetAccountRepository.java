package com.acco.life.repository;

import com.acco.life.entity.AssetAccount;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.data.r2dbc.repository.Query;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Flux;

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

    @Query("""
        SELECT *
        FROM asset_account
        WHERE (:name IS NULL OR name LIKE :name)
          AND (:platformCode IS NULL OR platform_code LIKE :platformCode)
          AND (:accountNumber IS NULL OR account_number LIKE :accountNumber)
          AND (:userId IS NULL OR user_id = :userId)
        ORDER BY id DESC
        LIMIT :limit OFFSET :offset
    """)
    Flux<AssetAccount> search(String name, String platformCode, String accountNumber, Integer userId, long limit, long offset);

    @Query("""
        SELECT COUNT(1)
        FROM asset_account
        WHERE (:name IS NULL OR name LIKE :name)
          AND (:platformCode IS NULL OR platform_code LIKE :platformCode)
          AND (:accountNumber IS NULL OR account_number LIKE :accountNumber)
          AND (:userId IS NULL OR user_id = :userId)
    """)
    Mono<Long> countSearch(String name, String platformCode, String accountNumber, Integer userId);
}