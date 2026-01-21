package com.acco.life.repository.fin;

import com.acco.life.entity.fin.FinStatementFile;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * 账单文件导入日志 Repository
 *
 * @author wensen.zhang
 * @version V2.0.0
 */
public interface FinStatementFileRepository extends ReactiveCrudRepository<FinStatementFile, Long> {

    /**
     * 根据文件MD5查询（幂等检查）
     */
    Mono<FinStatementFile> findByFileMd5(String fileMd5);

    /**
     * 根据用户ID查询
     */
    Flux<FinStatementFile> findByUserId(Long userId);

    /**
     * 根据用户ID和状态查询
     */
    Flux<FinStatementFile> findByUserIdAndStatus(Long userId, String status);

    /**
     * 根据用户ID和平台代码查询
     */
    Flux<FinStatementFile> findByUserIdAndPlatformCode(Long userId, String platformCode);

    /**
     * 分页查询
     */
    @Query("""
        SELECT * FROM fin_statement_file 
        WHERE (:userId IS NULL OR user_id = :userId)
          AND (:platformCode IS NULL OR platform_code = :platformCode)
          AND (:status IS NULL OR status = :status)
        ORDER BY uploaded_at DESC
        LIMIT :limit OFFSET :offset
    """)
    Flux<FinStatementFile> search(Long userId, String platformCode, String status, long limit, long offset);

    /**
     * 统计查询
     */
    @Query("""
        SELECT COUNT(1) FROM fin_statement_file 
        WHERE (:userId IS NULL OR user_id = :userId)
          AND (:platformCode IS NULL OR platform_code = :platformCode)
          AND (:status IS NULL OR status = :status)
    """)
    Mono<Long> countSearch(Long userId, String platformCode, String status);
}
