package com.acco.life.repository;

import com.acco.life.entity.CreditWalletStatement;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

/**
 * description: 信用钱包账单仓库接口，提供信用钱包账单的数据库操作
 *
 * @date: 2025-07-24 17:54:23
 * @author wensen.zhang
 * @version V1.0.0
 */
public interface CreditWalletStatementRepository extends ReactiveCrudRepository<CreditWalletStatement, Long> {
}
