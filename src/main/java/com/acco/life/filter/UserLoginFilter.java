package com.acco.life.filter;

import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

/**
 * description: 用户登录过滤器，用于从请求头中提取用户ID并放入上下文中
 *
 * @date: 2025-07-24 17:54:23
 * @author wensen.zhang
 * @version V1.0.0
 */
@Component
public class UserLoginFilter implements WebFilter {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        String userId = exchange.getRequest().getHeaders().getFirst("userId");

        if (userId != null && !userId.isEmpty()) {
            // 将 userId 放入 reactor 上下文，供全链路使用
            return chain.filter(exchange)
                    .contextWrite(ctx -> ctx.put("userId", userId));
        }

        return chain.filter(exchange);
    }
}