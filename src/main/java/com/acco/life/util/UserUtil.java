package com.acco.life.util;

import com.acco.life.dto.UserDto;
import com.acco.life.service.UserService;
import reactor.core.publisher.Mono;

/**
 *
 * @author wensenzhang
 * @version V1.0.0
 * @date 2025/7/17 20:27
 */
public abstract class UserUtil {

    public static Mono<UserDto> getCurrentUser() {
        return Mono.deferContextual(ctx -> {
            String userId = ctx.get("userId");
            if (userId.isEmpty()) {
                return Mono.empty();
            }
           return SpringContextUtil.getBean(UserService.class).findById(Integer.parseInt(userId));
        });
    }

}
