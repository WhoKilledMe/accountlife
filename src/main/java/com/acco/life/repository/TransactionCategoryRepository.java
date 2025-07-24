package com.acco.life.repository;

import com.acco.life.entity.TransactionCategory;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

/**
 * description: 交易分类仓库接口，提供交易分类的数据库操作
 *
 * @date: 2025-07-24 17:54:23
 * @author wensen.zhang
 * @version V1.0.0
 */
public interface TransactionCategoryRepository extends ReactiveCrudRepository<TransactionCategory, Integer> {
}
