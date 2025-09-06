package com.acco.life.repository;

import com.acco.life.entity.PlatformTransaction;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

/**
 * description: 平台交易仓库接口，提供平台交易的数据库操作
 *
 * @date: 2025-07-24 17:54:23
 * @author wensen.zhang
 * @version V1.0.0
 */
public interface PlatformTransactionRepository extends ReactiveCrudRepository<PlatformTransaction, Long> {
}
