package com.acco.life.repository;

import com.acco.life.entity.BaseColumnEntity;
import com.acco.life.util.SpringContextUtil;
import com.acco.life.util.UserUtil;
import org.reactivestreams.Publisher;
import org.springframework.data.r2dbc.mapping.OutboundRow;
import org.springframework.data.r2dbc.mapping.event.BeforeConvertCallback;
import org.springframework.data.r2dbc.mapping.event.BeforeSaveCallback;
import org.springframework.data.relational.core.sql.SqlIdentifier;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.time.LocalDateTime;

/**
 * description: [此处简要描述文件功能]
 *
 * @author wensen.zhang
 * @version V1.0.0
 * @date: 2025-09-01 16:34:46
 */
@Component
public class AuditFieldCallback<T extends BaseColumnEntity> implements BeforeConvertCallback<T>, BeforeSaveCallback<T> {


    @Override
    public Publisher<T> onBeforeConvert(T entity, SqlIdentifier table) {
        return UserUtil.getCurrentUserId().publishOn(Schedulers.boundedElastic()).flatMap(
                userId -> {
                    LocalDateTime now = LocalDateTime.now();
                    if (entity.getId() == null) {
                        // 插入
                        entity.setCreatedAt(now);
                        entity.setCreatedBy(String.valueOf(userId));
                        entity.setIsDeleted(0);
                        // 插入/更新都要
                        entity.setUpdatedAt(now);
                        entity.setUpdatedBy(String.valueOf(userId));
                        return Mono.just(entity);
                    } else {
                        String className = entity.getClass().getSimpleName() + "Repository";
                        String lowerCamelName = className.substring(0,1).toLowerCase() + className.substring(1);
                        @SuppressWarnings("unchecked")
                        ReactiveCrudRepository<T, Object> bean = SpringContextUtil.getBean(lowerCamelName, ReactiveCrudRepository.class);
                        return (bean.findById(entity.getId()))
                                .defaultIfEmpty(entity)
                                .map(dbEntity -> {
                                    if (dbEntity != null && dbEntity.getId() != null) {
                                        entity.setCreatedAt(dbEntity.getCreatedAt());
                                        entity.setCreatedBy(dbEntity.getCreatedBy());
                                        entity.setIsDeleted(dbEntity.getIsDeleted());
                                    }
                                    entity.setUpdatedAt(now);
                                    entity.setUpdatedBy(String.valueOf(userId));
                                    return entity;
                                });
                    }
                }
        );

    }

    @Override
    public Publisher<T> onBeforeSave(T entity, OutboundRow row, SqlIdentifier table) {
        // 如果需要对 row 进行额外处理，可以写在这里
        return Mono.just(entity);
    }


}
