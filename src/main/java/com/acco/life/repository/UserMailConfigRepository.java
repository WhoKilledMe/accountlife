package com.acco.life.repository;

import com.acco.life.entity.UserMailConfig;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.data.r2dbc.repository.Query;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface UserMailConfigRepository extends ReactiveCrudRepository<UserMailConfig, Long> {

    @Query("""
        SELECT *
        FROM user_mail_config
        WHERE (:userId IS NULL OR user_id = :userId)
          AND (:name IS NULL OR name LIKE :name)
        ORDER BY id DESC
        LIMIT :limit OFFSET :offset
    """)
    Flux<UserMailConfig> search(Long userId, String name, long limit, long offset);

    @Query("""
        SELECT COUNT(1)
        FROM user_mail_config
        WHERE (:userId IS NULL OR user_id = :userId)
          AND (:name IS NULL OR name LIKE :name)
    """)
    Mono<Long> countSearch(Long userId, String name);
}


