package com.acco.life.controller;

import com.acco.life.dto.FixedAssetDto;
import com.acco.life.service.FixedAssetService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * description: 固定资产控制器，提供固定资产的增删改查接口
 *
 * @date: 2025-07-24 17:54:23
 * @author wensen.zhang
 * @version V1.0.0
 */
@Tag(name = "FixedAsset 接口")
@RestController
@RequestMapping("/api/fixedasset")
@RequiredArgsConstructor
public class FixedAssetController {

    private final FixedAssetService service;

    @Operation(summary = "查询所有 FixedAsset")
    @GetMapping
    public Mono<ResponseEntity<List<FixedAssetDto>>> list() {
        return service.findAll().map(ResponseEntity::ok)
                    .onErrorResume(e ->
                    Mono.just(ResponseEntity.badRequest().build()));
    }

    @Operation(summary = "根据 ID 查询 FixedAsset")
    @GetMapping("/{id}")
    public Mono<ResponseEntity<FixedAssetDto>> get(@PathVariable Integer id) {
        return service.findById(id).map(ResponseEntity::ok)
        .onErrorResume(e ->
        Mono.just(ResponseEntity.badRequest().build()));
    }

    @Operation(summary = "创建 FixedAsset")
    @PostMapping
    public Mono<ResponseEntity<FixedAssetDto>> create(@RequestBody Mono<FixedAssetDto> dto) {
        return service.save(dto).map(ResponseEntity::ok)
                .onErrorResume(e ->
                Mono.just(ResponseEntity.badRequest().build()));
    }

    @Operation(summary = "更新 FixedAsset")
    @PutMapping
    public Mono<ResponseEntity<FixedAssetDto>> update(@RequestBody Mono<FixedAssetDto> dto) {
        return service.save(dto).map(ResponseEntity::ok)
                    .onErrorResume(e ->
                    Mono.just(ResponseEntity.badRequest().build()));
    }

    @Operation(summary = "删除 FixedAsset")
    @DeleteMapping("/{id}")
    public Mono<Void> delete(@PathVariable Integer id) {
        return service.deleteById(id);
    }
}
