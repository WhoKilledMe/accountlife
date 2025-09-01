package com.acco.life.repository;

import com.acco.life.entity.BaseColumnEntity;
import com.acco.life.util.SpringContextUtil;
import com.acco.life.util.UserUtil;
import org.reactivestreams.Publisher;
import org.springframework.beans.BeanUtils;
import org.springframework.data.r2dbc.mapping.OutboundRow;
import org.springframework.data.r2dbc.mapping.event.BeforeConvertCallback;
import org.springframework.data.r2dbc.mapping.event.BeforeSaveCallback;
import org.springframework.data.relational.core.sql.SqlIdentifier;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.lang.reflect.Field;
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
    public Publisher<T> onBeforeConvert(@NonNull T entity, @NonNull SqlIdentifier table) {
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
                                        // 使用 BeanUtils 复制属性，只复制 entity 中为 null 的字段
                                        copyNonNullFields(dbEntity, entity);
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
    public Publisher<T> onBeforeSave(@NonNull T entity, @NonNull OutboundRow row, @NonNull SqlIdentifier table) {
        // 如果需要对 row 进行额外处理，可以写在这里
        return Mono.just(entity);
    }

    /**
     * 复制源对象中非null的属性到目标对象中为null的字段
     * @param source 源对象
     * @param target 目标对象
     */
    private void copyNonNullFields(Object source, Object target) {
        // 使用 BeanUtils 复制属性，排除目标对象中非null的字段
        BeanUtils.copyProperties(source, target, getNonNullPropertyNames(target));
    }

    /**
     * 获取对象中非null的属性名数组
     * @param target 目标对象
     * @return 非null的属性名数组
     */
    private String[] getNonNullPropertyNames(Object target) {
        try {
            Field[] fields = target.getClass().getDeclaredFields();
            java.util.List<String> nonNullPropertyNames = new java.util.ArrayList<>();
            for (Field field : fields) {
                field.setAccessible(true);
                Object value = field.get(target);
                if (value != null) {
                    nonNullPropertyNames.add(field.getName());
                }
            }
            return nonNullPropertyNames.toArray(new String[0]);
        } catch (Exception e) {
            return new String[0];
        }
    }

}
