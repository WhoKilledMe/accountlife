package com.acco.life.controller;

import com.acco.life.dto.FixedAssetDto;
import com.acco.life.service.FixedAssetService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Tag(name = "FixedAsset 接口")
@RestController
@RequestMapping("/api/fixedasset")
@RequiredArgsConstructor
public class FixedAssetController {

    private final FixedAssetService service;

    @Operation(summary = "查询所有 FixedAsset")
    @GetMapping
    public Flux<FixedAssetDto> list() {
        return service.findAll();
    }

    @Operation(summary = "根据 ID 查询 FixedAsset")
    @GetMapping("/{id}")
    public Mono<FixedAssetDto> get(@PathVariable Integer id) {
        return service.findById(id);
    }

    @Operation(summary = "创建 FixedAsset")
    @PostMapping
    public Mono<FixedAssetDto> create(@RequestBody Mono<FixedAssetDto> dto) {
        return service.save(dto);
    }

    @Operation(summary = "更新 FixedAsset")
    @PutMapping
    public Mono<FixedAssetDto> update(@RequestBody Mono<FixedAssetDto> dto) {
        return service.save(dto);
    }

    @Operation(summary = "删除 FixedAsset")
    @DeleteMapping("/{id}")
    public Mono<Void> delete(@PathVariable Integer id) {
        return service.deleteById(id);
    }
}
