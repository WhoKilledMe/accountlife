package com.acco.life.service.impl;

import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class TokenStore {

    private final Map<String, Integer> tokenToUser = new ConcurrentHashMap<>();
    private final Map<String, Long> tokenExpireAt = new ConcurrentHashMap<>();
    private Duration ttl = Duration.ofHours(12);

    public void setTtl(Duration ttl) {
        this.ttl = ttl;
    }

    public void store(String token, Integer userId) {
        tokenToUser.put(token, userId);
        tokenExpireAt.put(token, System.currentTimeMillis() + ttl.toMillis());
    }

    public Integer getUserId(String token) {
        if (token == null) {
            return null;
        }
        Long expireAt = tokenExpireAt.get(token);
        if (expireAt == null || expireAt < System.currentTimeMillis()) {
            invalidate(token);
            return null;
        }
        return tokenToUser.get(token);
    }

    public void invalidate(String token) {
        if (token != null) {
            tokenToUser.remove(token);
            tokenExpireAt.remove(token);
        }
    }
}


