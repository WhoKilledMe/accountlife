package com.acco.life;

import com.acco.life.entity.User;
import reactor.core.publisher.Mono;

/**
 * @author wensenzhang
 * @version V1.0.0
 * 星星之火，可以燎原
 * @desc UserUtil
 * @date 2025/7/17 20:27
 */
public abstract class UserUtil {

    public static Mono<User> getCurrentUser() {
        return Mono.deferContextual(ctx -> {
            User user = new User();
            String userId = ctx.get("userId");
            user.setId(Integer.parseInt(userId));
            return Mono.just(user);
        });
    }

}
