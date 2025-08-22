package com.acco.life.util;

import com.acco.life.dto.UserDto;
import com.acco.life.service.UserService;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

/**
 * description: 用户工具类，提供获取当前用户信息的功能
 * 
 * @date: 2025-07-24 17:54:23
 * @author wensen.zhang
 * @version V1.0.0
 */
@Slf4j
public abstract class UserUtil {

    /**
     * 获取当前用户信息
     * 
     * 从反应式上下文中获取用户ID，如果用户ID为空则返回空的Mono，
     * 否则通过UserService根据用户ID查询用户信息
     * 
     * @return 包含用户信息的Mono对象
     */
    public static Mono<UserDto> getCurrentUser() {

        return Mono.deferContextual(ctx -> {
            log.debug("正在尝试从Reactor上下文中获取userId");
            
            // 检查上下文中是否存在userId键
            if (!ctx.hasKey("userId")) {
                log.warn("Reactor上下文中没有找到userId键");
                return Mono.empty();
            }
            
            try {
                // 从上下文中获取userId
                String userId = ctx.get("userId");
                log.debug("从上下文中获取到userId: {}", userId);
                
                // 检查userId是否为空
                if (userId == null || userId.isEmpty()) {
                    log.warn("从上下文中获取到的userId为空");
                    return Mono.empty();
                }
                
                // 调用UserService根据ID查找用户
                log.debug("正在调用UserService.findById查找用户: {}", userId);
                return SpringContextUtil.getBean(UserService.class).findById(Integer.parseInt(userId))
                        .doOnSuccess(user -> log.debug("成功获取到用户信息: {}", user))
                        .doOnError(error -> log.error("获取用户信息时发生错误: {}", error.getMessage()));
            } catch (Exception e) {
                // 如果出现任何异常，返回空的Mono
                log.error("获取用户信息时发生异常: {}", e.getMessage(), e);
                return Mono.empty();
            }
        });
    }

    /**
     * 直接从上下文中获取userId字符串
     * 
     * @return 包含userId的Mono对象
     */
    public static Mono<Integer> getCurrentUserId() {
        return Mono.deferContextual(ctx -> {
            if (ctx.hasKey("userId")) {
                String userId = ctx.get("userId");
                log.debug("直接获取到userId: {}", userId);
                return Mono.just(Integer.valueOf(userId));
            } else {
                log.warn("上下文中没有userId键");
                return Mono.empty();
            }
        });
    }
}