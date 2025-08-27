package com.acco.life.controller;

import com.acco.life.util.UserUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

/**
 * description: 测试控制器，用于验证userId传递
 *
 * @date: 2025-08-12 20:59:59
 * @author wensen.zhang
 * @version V1.0.0
 */
@Tag(name = "Test 接口")
@RestController
@RequestMapping("/api/test")
@Slf4j
public class TestController {

    @Operation(summary = "测试获取当前用户")
    @GetMapping("/current-user")
    public Mono<ResponseEntity<String>> getCurrentUser() {
        log.info("开始测试getCurrentUser方法");
        
        return UserUtil.getCurrentUser()
                .map(user -> {
                    log.info("成功获取到用户: {}", user);
                    return ResponseEntity.ok("当前用户ID: " + user.getId());
                })
                .defaultIfEmpty(ResponseEntity.ok("未获取到用户信息"))
                .doOnSubscribe(s -> log.info("订阅getCurrentUser流"))
                .doOnNext(result -> log.info("getCurrentUser结果: {}", result))
                .doOnError(error -> log.error("getCurrentUser发生错误: {}", error.getMessage()));
    }

    @Operation(summary = "测试直接获取userId")
    @GetMapping("/user-id")
    public Mono<ResponseEntity<String>> getUserId() {
        log.info("开始测试getUserId方法");
        
        return Mono.deferContextual(ctx -> {
            log.info("当前Reactor上下文内容: {}", ctx);
            
            if (ctx.hasKey("userId")) {
                String userId = ctx.get("userId");
                log.info("从上下文中获取到userId: {}", userId);
                return Mono.just(ResponseEntity.ok("从上下文获取到userId: " + userId));
            } else {
                log.warn("上下文中没有userId键");
                return Mono.just(ResponseEntity.ok("上下文中没有userId"));
            }
        })
        .doOnSubscribe(s -> log.info("订阅getUserId流"))
        .doOnNext(result -> log.info("getUserId结果: {}", result))
        .doOnError(error -> log.error("getUserId发生错误: {}", error.getMessage()));
    }

    @Operation(summary = "测试直接获取userId（带请求头参数）")
    @GetMapping("/user-id-header")
    public Mono<ResponseEntity<String>> getUserIdFromHeader(@RequestHeader(value = "userId", required = false) String headerUserId) {
        log.info("请求头中的userId: {}", headerUserId);
        
        return Mono.deferContextual(ctx -> {
            log.info("当前Reactor上下文内容: {}", ctx);
            
            if (ctx.hasKey("userId")) {
                String contextUserId = ctx.get("userId");
                log.info("从上下文中获取到userId: {}", contextUserId);
                return Mono.just(ResponseEntity.ok(String.format("请求头userId: %s, 上下文userId: %s", headerUserId, contextUserId)));
            } else {
                log.warn("上下文中没有userId键");
                return Mono.just(ResponseEntity.ok(String.format("请求头userId: %s, 上下文中没有userId", headerUserId)));
            }
        });
    }

    @Operation(summary = "测试UserUtil.getCurrentUserId方法")
    @GetMapping("/current-user-id")
    public Mono<ResponseEntity<String>> getCurrentUserId() {
        log.info("开始测试getCurrentUserId方法");
        
        return UserUtil.getCurrentUserId()
                .map(userId -> {
                    log.info("成功获取到userId: {}", userId);
                    return ResponseEntity.ok("当前用户ID: " + userId);
                })
                .defaultIfEmpty(ResponseEntity.ok("未获取到userId"))
                .doOnSubscribe(s -> log.info("订阅getCurrentUserId流"))
                .doOnNext(result -> log.info("getCurrentUserId结果: {}", result))
                .doOnError(error -> log.error("getCurrentUserId发生错误: {}", error.getMessage()));
    }
} 