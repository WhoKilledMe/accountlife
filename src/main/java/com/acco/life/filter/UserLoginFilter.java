package com.acco.life.filter;

import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

@Component
public class UserLoginFilter implements WebFilter {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        String userId = exchange.getRequest().getHeaders().getFirst("userId");

        if (userId != null && !userId.isEmpty()) {
            // 将 userId 放入 exchange 的 attributes 中，供后续使用
            exchange.getAttributes().put("userId", userId);
        }

        return chain.filter(exchange);
    }
}

