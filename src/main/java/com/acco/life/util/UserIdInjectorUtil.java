package com.acco.life.util;

import reactor.core.publisher.Mono;
import java.lang.reflect.Method;

/**
 * description: UserId注入工具类，用于自动从上下文获取userId并设置到DTO对象中
 *
 * @date: 2025-07-24 17:54:23
 * @author wensen.zhang
 * @version V1.0.0
 */
public class UserIdInjectorUtil {
    
    /**
     * 自动通过反射设置DTO对象的userId字段
     *
     * @param dtoMono DTO对象的Mono流
     * @param <T> DTO类型
     * @return 设置userId后的DTO对象的Mono流
     */
    public static <T> Mono<T> withUserId(Mono<T> dtoMono) {
        return UserUtil.getCurrentUser()
                .flatMap(userDto -> {
                    Long userId = userDto.getId();
                    return dtoMono.map(dto -> {
                        try {
                            // 通过反射设置userId
                            Method setUserIdMethod = dto.getClass().getMethod("setUserId", Integer.class);
                            setUserIdMethod.invoke(dto, userId);
                        } catch (Exception e) {
                            // 如果没有setUserId方法或设置失败，则忽略
                        }
                        return dto;
                    });
                });
    }

    /**
     * 自动通过反射设置DTO对象的userId字段并保存
     *
     * @param dtoMono DTO对象的Mono流
     * @param service 服务对象，用于保存DTO
     * @param <T> DTO类型
     * @param <R> 返回结果类型
     * @return 保存结果的Mono流
     */
    public static <T, R> Mono<R> withUserIdAndSave(Mono<T> dtoMono, Object service) {
        return withUserId(dtoMono)
                .flatMap(dto -> {
                    try {
                        // 通过反射调用服务的save方法
                        Method saveMethod = service.getClass().getMethod("save", Mono.class);
                        return (Mono<R>) saveMethod.invoke(service, Mono.just(dto));
                    } catch (Exception e) {
                        throw new RuntimeException("Failed to save entity", e);
                    }
                });
    }
    
    /**
     * 自动注入userId到DTO对象并执行操作
     *
     * @param dtoMono DTO对象的Mono流
     * @param function 处理函数，接收DTO对象和userId
     * @param <T> DTO类型
     * @param <R> 返回结果类型
     * @return 处理结果的Mono流
     */
    public static <T, R> Mono<R> withUserId(Mono<T> dtoMono, java.util.function.BiFunction<T, Long, R> function) {
        return UserUtil.getCurrentUser()
                .flatMap(userDto -> {

                    Long userId = userDto.getId();
                    return dtoMono.map(dto -> function.apply(dto, userId));
                });
    }
}