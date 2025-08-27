package com.acco.life.filter;

import com.acco.life.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.lang.NonNull;
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

    @Autowired
    private AuthService authService;

    @Override
    public @NonNull Mono<Void> filter(@NonNull ServerWebExchange exchange, @NonNull WebFilterChain chain) {

        String path = exchange.getRequest().getPath().value();
        if ("/api/auth/login".equals(path) || "/api/auth/logout".equals(path)) {
            return chain.filter(exchange);
        }

        String tokenHeader = exchange.getRequest().getHeaders().getFirst("token");
        String authorization = exchange.getRequest().getHeaders().getFirst("Authorization");
        String token = null;
        if (authorization != null && authorization.startsWith("Bearer ")) {
            token = authorization.substring(7);
        }
        if (token == null && tokenHeader != null && !tokenHeader.isEmpty()) {
            token = tokenHeader;
        }

        if (token == null) {
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }

        return authService.getUserIdByToken(token)
                .flatMap(uid -> chain.filter(exchange)
                        .contextWrite(ctx -> ctx.put("userId", String.valueOf(uid))))
                .switchIfEmpty(Mono.defer(() -> {
                    exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                    return exchange.getResponse().setComplete();
                }));
    }
}