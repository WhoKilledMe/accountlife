package com.acco.life.controller;

import com.acco.life.dto.AssetAccountDto;
import com.acco.life.service.AssetAccountService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Tag(name = "AssetAccount 接口")
@RestController
@RequestMapping("/api/assetaccount")
@RequiredArgsConstructor
public class AssetAccountController {

    private final AssetAccountService service;

    @Operation(summary = "查询所有 AssetAccount")
    @GetMapping
    public Flux<AssetAccountDto> list() {
        return service.findAll();
    }

    @Operation(summary = "根据 ID 查询 AssetAccount")
    @GetMapping("/{id}")
    public Mono<AssetAccountDto> get(@PathVariable Integer id) {
        return service.findById(id);
    }

    @Operation(summary = "创建 AssetAccount")
    @PostMapping
    public Mono<AssetAccountDto> create(@RequestBody Mono<AssetAccountDto> dto) {
        return service.save(dto);
    }

    @Operation(summary = "更新 AssetAccount")
    @PutMapping
    public Mono<AssetAccountDto> update(@RequestBody Mono<AssetAccountDto> dto) {
        return service.save(dto);
    }

    @Operation(summary = "删除 AssetAccount")
    @DeleteMapping("/{id}")
    public Mono<Void> delete(@PathVariable Integer id) {
        return service.deleteById(id);
    }
}
