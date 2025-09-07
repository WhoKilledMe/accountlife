package com.acco.life.repository;

import com.acco.life.entity.AccountTransactionUploadFileLog;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface AccountTransactionUploadFileLogRepository extends ReactiveCrudRepository<AccountTransactionUploadFileLog, Long> {

    @Query("""
                SELECT *
                FROM account_transaction_upload_file_log
                WHERE (:status IS NULL OR status = :status)
                  AND (:userId IS NULL OR user_id = :userId)
                  AND (:fileName IS NULL OR file_name LIKE :fileName)
                  AND (:accountId IS NULL OR account_id = :accountId)
                ORDER BY id DESC
                LIMIT :limit OFFSET :offset
            """)
    Flux<AccountTransactionUploadFileLog> search(String status, Long userId, String fileName, Long accountId, long limit, long offset);

    @Query("""
                SELECT COUNT(1)
                FROM account_transaction_upload_file_log
                WHERE (:status IS NULL OR status = :status)
                  AND (:userId IS NULL OR user_id = :userId)
                  AND (:fileName IS NULL OR file_name LIKE :fileName)
                  AND (:accountId IS NULL OR account_id = :accountId)
            
            """)
    Mono<Long> countSearch(String status, Long userId, String fileName, Long accountId);
}


