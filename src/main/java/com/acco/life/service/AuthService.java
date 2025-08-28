package com.acco.life.service;

import reactor.core.publisher.Mono;

public interface AuthService {
    Mono<String> login(String username, String password);
    Mono<Void> logout(String token);
    Mono<Integer> getUserIdByToken(String token);
}


