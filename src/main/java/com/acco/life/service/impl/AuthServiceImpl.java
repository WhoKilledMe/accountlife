package com.acco.life.service.impl;

import com.acco.life.repository.UserRepository;
import com.acco.life.service.AuthService;
import com.acco.life.util.PasswordUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final TokenStore tokenStore;

    @Override
    public Mono<String> login(String username, String password) {
        return userRepository.findByUsername(username)
                .flatMap(user -> {
                    String stored = user.getPassword();
                    if (PasswordUtil.verify(password, stored)) {
                        String token = UUID.randomUUID().toString().replace("-", "");
                        tokenStore.store(token, user.getId());
                        return Mono.just(token);
                    }
                    return Mono.error(new IllegalArgumentException("用户名或密码错误"));
                })
                .switchIfEmpty(Mono.error(new IllegalArgumentException("用户名或密码错误")));
    }

    @Override
    public Mono<Void> logout(String token) {
        if (token != null) {
            tokenStore.invalidate(token);
        }
        return Mono.empty();
    }

    @Override
    public Mono<Integer> getUserIdByToken(String token) {
        return Mono.justOrEmpty(tokenStore.getUserId(token));
    }
}


