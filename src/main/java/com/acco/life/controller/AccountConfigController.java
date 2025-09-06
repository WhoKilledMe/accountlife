package com.acco.life.controller;

import com.acco.life.dto.AccountConfigDto;
import com.acco.life.service.AccountConfigService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * 账户配置控制器
 *
 * @author wensen.zhang
 * @version V1.0.0
 */
@Tag(name = "AccountConfig 接口")
@RestController
@RequestMapping("/api/accountconfig")
@RequiredArgsConstructor
public class AccountConfigController {
    
    private final AccountConfigService service;
    
    @Operation(summary = "查询所有启用的账户配置")
    @GetMapping
    public Mono<ResponseEntity<List<AccountConfigDto>>> list() {
        return service.getAllForSelect()
                .map(ResponseEntity::ok)
                .onErrorResume(e -> Mono.just(ResponseEntity.badRequest().build()));
    }
    
    @Operation(summary = "根据类型查询账户配置")
    @GetMapping("/type/{type}")
    public Mono<ResponseEntity<List<AccountConfigDto>>> findByType(@PathVariable Integer type) {
        return service.findByType(type)
                .collectList()
                .map(ResponseEntity::ok)
                .onErrorResume(e -> Mono.just(ResponseEntity.badRequest().build()));
    }
    
    @Operation(summary = "根据名称模糊查询账户配置")
    @GetMapping("/search")
    public Mono<ResponseEntity<List<AccountConfigDto>>> search(@RequestParam(required = false) String name) {
        return service.findByNameLike(name)
                .collectList()
                .map(ResponseEntity::ok)
                .onErrorResume(e -> Mono.just(ResponseEntity.badRequest().build()));
    }
}
