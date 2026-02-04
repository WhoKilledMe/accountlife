package com.acco.life.repository;

import reactor.core.publisher.Mono;

import java.util.List;

/**
 * 批量插入 Repository 接口
 * 所有需要批量插入功能的 Repository 可以实现此接口
 *
 * @author wensen.zhang
 * @version V2.0.0
 */
public interface BatchInsertRepository<T> {

    /**
     * 批量插入实体
     * 
     * @param entities 实体列表
     * @return 成功插入的记录数
     */
    Mono<Integer> batchInsert(List<T> entities);

    /**
     * 批量插入实体（使用 INSERT IGNORE）
     * 
     * @param entities 实体列表
     * @return 成功插入的记录数
     */
    Mono<Integer> batchInsertIgnore(List<T> entities);
}
