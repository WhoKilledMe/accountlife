package com.acco.life.controller;

import com.acco.life.dto.AssetAccountDto;
import com.acco.life.service.AssetAccountService;
import com.acco.life.util.UserIdInjectorUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * description: 资产账户控制器，提供资产账户的增删改查接口
 *
 * @date: 2025-07-24 17:54:23
 * @author wensen.zhang
 * @version V1.0.0
 */
@Tag(name = "AssetAccount 接口")
@RestController
@RequestMapping("/api/assetaccount")
@RequiredArgsConstructor
public class AssetAccountController {

    private final AssetAccountService service;

    @Operation(summary = "查询所有 AssetAccount")
    @GetMapping
    public Mono<ResponseEntity<List<AssetAccountDto>>> list() {
        return service.findAll().map(ResponseEntity::ok)
                    .onErrorResume(e ->
                    Mono.just(ResponseEntity.badRequest().build()));
    }

    @Operation(summary = "根据 ID 查询 AssetAccount")
    @GetMapping("/{id}")
    public Mono<ResponseEntity<AssetAccountDto>> get(@PathVariable Integer id) {
        return service.findById(id).map(ResponseEntity::ok)
        .onErrorResume(e ->
        Mono.just(ResponseEntity.badRequest().build()));
    }

    @Operation(summary = "创建 AssetAccount")
    @PostMapping
    public Mono<ResponseEntity<AssetAccountDto>> create(@RequestBody Mono<AssetAccountDto> dto) {
        return UserIdInjectorUtil.withUserId(dto)
                .flatMap(d -> service.save(Mono.just(d)))
                .map(ResponseEntity::ok)
                .onErrorResume(e ->
                        Mono.just(ResponseEntity.badRequest().build()));
    }

    @Operation(summary = "更新 AssetAccount")
    @PutMapping
    public Mono<ResponseEntity<AssetAccountDto>> update(@RequestBody Mono<AssetAccountDto> dto) {
        return UserIdInjectorUtil.withUserId(dto)
                .flatMap(d -> service.save(Mono.just(d)))
                .map(ResponseEntity::ok)
                .onErrorResume(e ->
                        Mono.just(ResponseEntity.badRequest().build()));
    }

    @Operation(summary = "删除 AssetAccount")
    @DeleteMapping("/{id}")
    public Mono<Void> delete(@PathVariable Integer id) {
        return service.deleteById(id);
    }
}