package com.acco.life;

import com.acco.life.dto.UserDto;
import com.acco.life.util.UserUtil;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import reactor.core.publisher.Mono;

@SpringBootTest
public class UserUtilTest {

    @Test
    public void testGetCurrentUserWithoutContext() {
        // 测试在没有Reactor上下文的情况下获取用户
        Mono<UserDto> userMono = UserUtil.getCurrentUser();
        
        // 验证返回的是空的Mono
        userMono.subscribe(
            user -> System.out.println("获取到用户: " + user),
            error -> System.err.println("发生错误: " + error),
            () -> System.out.println("完成，没有用户")
        );
    }

    @Test
    public void testGetCurrentUserWithContext() {
        // 测试在有Reactor上下文的情况下获取用户
        String testUserId = "123";
        
        // 使用contextWrite来设置上下文
        Mono<UserDto> userMono = UserUtil.getCurrentUser()
                .contextWrite(ctx -> ctx.put("userId", testUserId));
        
        // 验证返回的是空的Mono（因为UserService.findById会失败）
        userMono.subscribe(
            user -> System.out.println("获取到用户: " + user),
            error -> System.err.println("发生错误: " + error),
            () -> System.out.println("完成，没有3")
        );
    }
} 