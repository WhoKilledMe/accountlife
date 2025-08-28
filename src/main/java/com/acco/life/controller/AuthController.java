package com.acco.life.controller;

import com.acco.life.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@Tag(name = "Auth 接口")
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @Operation(summary = "登录，返回token")
    @PostMapping("/login")
    public Mono<ResponseEntity<TokenResp>> login(@RequestBody Mono<LoginReq> reqMono) {
        return reqMono.flatMap(req -> authService.login(req.getUsername(), req.getPassword()))
                .map(token -> ResponseEntity.ok(new TokenResp(token)))
                .onErrorResume(e -> Mono.just(ResponseEntity.badRequest().build()));
    }

    @Operation(summary = "登出，清理token")
    @PostMapping("/logout")
    public Mono<ResponseEntity<Void>> logout(@RequestHeader(name = "Authorization", required = false) String authorization) {
        String token = extractToken(authorization);
        return authService.logout(token).thenReturn(ResponseEntity.ok().build());
    }

    private String extractToken(String authorization) {
        if (authorization != null && authorization.startsWith("Bearer ")) {
            return authorization.substring(7);
        }
        return authorization;
    }

    @Data
    public static class LoginReq {
        private String username;
        private String password;
    }

    @Data
    public static class TokenResp {
        private final String token;
    }
}


