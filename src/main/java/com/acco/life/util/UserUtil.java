package com.acco.life.util;

import com.acco.life.dto.UserDto;
import com.acco.life.service.UserService;
import reactor.core.publisher.Mono;

/**
 * description: 用户工具类，提供获取当前用户信息的功能
 * 
 * @date: 2025-07-24 17:54:23
 * @author wensen.zhang
 * @version V1.0.0
 */
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
            if (!ctx.hasKey("userId")) {
                return Mono.empty();
            }
            String userId = ctx.get("userId");
            if (userId == null || userId.isEmpty()) {
                return Mono.empty();
            }
            return SpringContextUtil.getBean(UserService.class).findById(Integer.parseInt(userId));
        });
    }

}
