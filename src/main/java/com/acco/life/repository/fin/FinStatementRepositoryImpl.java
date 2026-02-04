package com.acco.life.repository.fin;

import com.acco.life.entity.fin.FinStatement;
import com.acco.life.util.ReactiveBatchInserter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * 账单行表 Repository 自定义实现
 * 提供批量插入等自定义方法
 * 
 * 注意：Spring Data R2DBC 的命名约定是 XxxRepositoryImpl
 * 这个类会被自动注入到 FinStatementRepository 中
 *
 * @author wensen.zhang
 * @version V2.0.0
 */
@Slf4j
@RequiredArgsConstructor
public class FinStatementRepositoryImpl {

    private final ReactiveBatchInserter batchInserter;

    /**
     * 批量插入账单行
     */
    public Mono<Integer> batchInsert(List<FinStatement> statements) {
        return batchInserter.insertBatch(statements, FinStatement.class, false);
    }

    /**
     * 批量插入账单行（使用 INSERT IGNORE）
     */
    public Mono<Integer> batchInsertIgnore(List<FinStatement> statements) {
        return batchInserter.insertBatch(statements, FinStatement.class, true);
    }
}
