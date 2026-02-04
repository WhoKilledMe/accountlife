package com.acco.life.util;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.data.relational.core.mapping.RelationalPersistentEntity;
import org.springframework.data.relational.core.mapping.RelationalPersistentProperty;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 响应式批量插入工具类
 * 提供真正的批量插入功能（多 VALUES SQL），所有 Repository 都可以使用
 *
 * @author wensen.zhang
 * @version V2.0.0
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ReactiveBatchInserter {

    private final R2dbcEntityTemplate template;
    private final DatabaseClient databaseClient;

    /**
     * 批量插入（真正多 VALUES SQL）
     * 
     * @param entities 实体列表
     * @param entityClass 实体类型
     * @param <T> 实体类型
     * @return 成功插入的记录数
     */
    public <T> Mono<Integer> insertBatch(List<T> entities, Class<T> entityClass) {
        return insertBatch(entities, entityClass, false);
    }

    /**
     * 批量插入（真正多 VALUES SQL）
     * 每100条记录插入一次，避免单次插入数据量过大
     * 
     * @param entities 实体列表
     * @param entityClass 实体类型
     * @param useInsertIgnore 是否使用 INSERT IGNORE（避免唯一键冲突）
     * @param <T> 实体类型
     * @return 成功插入的记录数
     */
    public <T> Mono<Integer> insertBatch(List<T> entities, Class<T> entityClass, boolean useInsertIgnore) {
        if (entities == null || entities.isEmpty()) {
            return Mono.just(0);
        }

        final int batchSize = 100; // 每批100条
        final int totalSize = entities.size();
        long startTime = System.currentTimeMillis();

        // 从上下文获取 userId
        return UserUtil.getCurrentUserId()
                .defaultIfEmpty(0L) // 如果上下文没有 userId，使用 0L
                .flatMap(userId -> {
                    // 将实体列表按100条分组，依次执行批量插入
                    return Flux.fromIterable(entities)
                            .buffer(batchSize)
                            .flatMap(batch -> {
                                log.debug("批量插入批次，批次大小: {}, 表名: {}", batch.size(), entityClass.getSimpleName());
                                return doInsertBatch(batch, entityClass, useInsertIgnore, userId)
                                        .onErrorResume(error -> {
                                            log.warn("批量插入批次失败，批次大小: {}, 错误: {}", batch.size(), error.getMessage(), error);
                                            return Mono.just(0); // 返回0表示该批次失败
                                        });
                            }, 1) // 限制并发为1，确保按顺序执行
                            .collectList()
                            .map(counts -> {
                                int successCount = counts.stream().mapToInt(Integer::intValue).sum();
                                long elapsed = System.currentTimeMillis() - startTime;
                                log.info("批量插入全部完成 - 实体类型: {}, 总数量: {}, 成功插入: {}, 批次数量: {}, 总耗时: {}ms (平均: {:.2f}ms/条)", 
                                        entityClass.getSimpleName(), totalSize, successCount, 
                                        (totalSize + batchSize - 1) / batchSize, elapsed,
                                        totalSize > 0 ? (double) elapsed / totalSize : 0);
                                return successCount;
                            });
                });
    }

    /**
     * 执行批量插入的内部方法
     */
    private <T> Mono<Integer> doInsertBatch(List<T> entities, Class<T> entityClass, boolean useInsertIgnore, Long userId) {
        long startTime = System.currentTimeMillis();
        final int entityCount = entities.size();
        
        try {
            RelationalPersistentEntity<?> entity = template.getConverter()
                    .getMappingContext()
                    .getRequiredPersistentEntity(entityClass);

            final String tableName = entity.getTableName().getReference();

            // 需要排除的字段列表（这些字段由数据库使用默认值）
            // created_at, updated_at, is_deleted 由数据库使用默认值
            // created_by, updated_by 需要从上下文 userId 自动设置
            List<String> excludedColumns = List.of("created_at", "updated_at", "is_deleted");

            // 获取所有需要插入的列（排除ID字段和部分审计字段）
            List<RelationalPersistentProperty> properties = new ArrayList<>();
            entity.forEach(p -> {
                // 排除ID字段（通常ID字段是自增的，不需要在INSERT中指定）
                if (!p.isIdProperty()) {
                    String columnName = p.getColumnName().getReference();
                    // 排除部分审计字段（created_at, updated_at, is_deleted 由数据库使用默认值）
                    // created_by, updated_by 需要插入，会从 userId 自动设置
                    if (!excludedColumns.contains(columnName)) {
                        properties.add(p);
                    }
                }
            });

            if (properties.isEmpty()) {
                log.warn("实体 {} 没有可插入的字段", entityClass.getSimpleName());
                return Mono.just(0);
            }

            List<String> columnNames = properties.stream()
                    .map(p -> p.getColumnName().getReference())
                    .collect(Collectors.toList());

            String columnPart = String.join(", ", columnNames);

            // 构建 VALUES 部分，使用命名参数
            StringBuilder valuesPart = new StringBuilder();
            for (int i = 0; i < entities.size(); i++) {
                if (i > 0) {
                    valuesPart.append(", ");
                }
                valuesPart.append("(");
                for (int j = 0; j < columnNames.size(); j++) {
                    if (j > 0) {
                        valuesPart.append(", ");
                    }
                    // 使用命名参数：:param0_0, :param0_1, :param1_0, ...
                    valuesPart.append(":param").append(i).append("_").append(j);
                }
                valuesPart.append(")");
            }

            // 构建 SQL
            String insertKeyword = useInsertIgnore ? "INSERT IGNORE INTO" : "INSERT INTO";
            String sql = String.format("%s %s (%s) VALUES %s", 
                    insertKeyword, tableName, columnPart, valuesPart);

            log.debug("批量插入SQL: {}", sql);
            log.debug("批量插入实体数量: {}, 表名: {}", entities.size(), tableName);

            // 构建参数绑定
            DatabaseClient.GenericExecuteSpec spec = databaseClient.sql(sql);

            for (int i = 0; i < entities.size(); i++) {
                T entityObj = entities.get(i);
                
                for (int j = 0; j < properties.size(); j++) {
                    RelationalPersistentProperty prop = properties.get(j);
                    String columnName = prop.getColumnName().getReference();
                    Object value = entity.getPropertyAccessor(entityObj).getProperty(prop);
                    String paramName = "param" + i + "_" + j;
                    
                    // 如果字段是 created_by 或 updated_by，且值为 null，且有 userId（从上下文获取），则使用 userId
                    if (("created_by".equals(columnName) || "updated_by".equals(columnName)) 
                            && value == null && userId != null && userId > 0) {
                        value = String.valueOf(userId);
                        //log.debug("自动设置 {} 字段为 userId: {}", columnName, userId);
                    }
                    
                    // R2DBC 要求 null 值必须使用 bindNull()，不能使用 bind()
                    if (value == null) {
                        // 获取字段类型用于 bindNull
                        Class<?> propertyType = prop.getType();
                        spec = spec.bindNull(paramName, propertyType);
                    } else {
                        spec = spec.bind(paramName, value);
                    }
                }
            }

            // 执行批量插入并返回影响的行数
            return spec.fetch().rowsUpdated()
                    .map(Long::intValue)
                    .doOnSuccess(count -> {
                        long elapsed = System.currentTimeMillis() - startTime;
                        log.info("批量插入完成 - 表名: {}, 实体数量: {}, 成功插入: {}, 耗时: {}ms (平均: {:.2f}ms/条)", 
                                tableName, entityCount, count, elapsed, 
                                entityCount > 0 ? (double) elapsed / entityCount : 0);
                    })
                    .doOnError(error -> {
                        long elapsed = System.currentTimeMillis() - startTime;
                        log.error("批量插入失败 - 表名: {}, 实体数量: {}, 耗时: {}ms", 
                                tableName, entityCount, elapsed, error);
                    });
        } catch (Exception e) {
            long elapsed = System.currentTimeMillis() - startTime;
            log.error("批量插入异常 - 实体类型: {}, 实体数量: {}, 耗时: {}ms", 
                    entityClass.getSimpleName(), entityCount, elapsed, e);
            return Mono.error(e);
        }
    }
}
