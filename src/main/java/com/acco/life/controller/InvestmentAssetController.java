package com.acco.life.controller;

import com.acco.life.dto.InvestmentAssetDto;
import com.acco.life.service.InvestmentAssetService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Tag(name = "InvestmentAsset 接口")
@RestController
@RequestMapping("/api/investmentasset")
@RequiredArgsConstructor
public class InvestmentAssetController {

    private final InvestmentAssetService service;

    @Operation(summary = "查询所有 InvestmentAsset")
    @GetMapping
    public Flux<InvestmentAssetDto> list() {
        return service.findAll();
    }

    @Operation(summary = "根据 ID 查询 InvestmentAsset")
    @GetMapping("/{id}")
    public Mono<InvestmentAssetDto> get(@PathVariable Integer id) {
        return service.findById(id);
    }

    @Operation(summary = "创建 InvestmentAsset")
    @PostMapping
    public Mono<InvestmentAssetDto> create(@RequestBody Mono<InvestmentAssetDto> dto) {
        return service.save(dto);
    }

    @Operation(summary = "更新 InvestmentAsset")
    @PutMapping
    public Mono<InvestmentAssetDto> update(@RequestBody Mono<InvestmentAssetDto> dto) {
        return service.save(dto);
    }

    @Operation(summary = "删除 InvestmentAsset")
    @DeleteMapping("/{id}")
    public Mono<Void> delete(@PathVariable Integer id) {
        return service.deleteById(id);
    }
}
