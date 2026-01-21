package com.acco.life.repository.fin;

import com.acco.life.entity.fin.FinAccount;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * 账户主表 Repository
 *
 * @author wensen.zhang
 * @version V2.0.0
 */
public interface FinAccountRepository extends ReactiveCrudRepository<FinAccount, Long> {

    /**
     * 根据用户ID查询所有账户
     */
    Flux<FinAccount> findByUserId(Long userId);

    /**
     * 根据用户ID和账户编码查询
     */
    Mono<FinAccount> findByUserIdAndAccountCode(Long userId, String accountCode);

    /**
     * 根据用户ID和平台代码查询
     */
    Flux<FinAccount> findByUserIdAndPlatformCode(Long userId, String platformCode);

    /**
     * 根据用户ID、平台代码和外部账户标识查询（去重用）
     */
    Mono<FinAccount> findByUserIdAndPlatformCodeAndExternalAccountRef(Long userId, String platformCode, String externalAccountRef);

    /**
     * 根据用户ID和账户大类查询
     */
    Flux<FinAccount> findByUserIdAndAccountCategory(Long userId, String accountCategory);

    /**
     * 根据用户ID和账户名称模糊查询
     */
    @Query("SELECT * FROM fin_account WHERE user_id = :userId AND account_name LIKE :accountName AND is_deleted = 0")
    Flux<FinAccount> findByUserIdAndAccountNameLike(Long userId, String accountName);

    /**
     * 分页查询
     */
    @Query("""
        SELECT * FROM fin_account 
        WHERE (:userId IS NULL OR user_id = :userId)
          AND (:platformCode IS NULL OR platform_code = :platformCode)
          AND (:accountCategory IS NULL OR account_category = :accountCategory)
          AND (:status IS NULL OR status = :status)
          AND is_deleted = 0
        ORDER BY id DESC
        LIMIT :limit OFFSET :offset
    """)
    Flux<FinAccount> search(Long userId, String platformCode, String accountCategory, String status, long limit, long offset);

    /**
     * 统计查询
     */
    @Query("""
        SELECT COUNT(1) FROM fin_account 
        WHERE (:userId IS NULL OR user_id = :userId)
          AND (:platformCode IS NULL OR platform_code = :platformCode)
          AND (:accountCategory IS NULL OR account_category = :accountCategory)
          AND (:status IS NULL OR status = :status)
          AND is_deleted = 0
    """)
    Mono<Long> countSearch(Long userId, String platformCode, String accountCategory, String status);
}
