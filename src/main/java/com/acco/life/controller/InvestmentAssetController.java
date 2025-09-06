package com.acco.life.controller;

import com.acco.life.dto.InvestmentAssetDto;
import com.acco.life.common.PageResponse;
import com.acco.life.service.InvestmentAssetService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * description: 投资资产控制器，提供投资资产的增删改查接口
 *
 * @date: 2025-07-24 17:54:23
 * @author wensen.zhang
 * @version V1.0.0
 */
@Tag(name = "InvestmentAsset 接口")
@RestController
@RequestMapping("/api/investmentasset")
@RequiredArgsConstructor
public class InvestmentAssetController {

    private final InvestmentAssetService service;

    @Operation(summary = "查询所有 InvestmentAsset")
    @GetMapping
    public Mono<ResponseEntity<List<InvestmentAssetDto>>> list() {
        return service.findAll().map(ResponseEntity::ok)
                    .onErrorResume(e ->
                    Mono.just(ResponseEntity.badRequest().build()));
    }

    @Operation(summary = "分页查询 InvestmentAsset")
    @GetMapping("/page")
    public Mono<ResponseEntity<PageResponse<InvestmentAssetDto>>> page(
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "10") int size
    ) {
        return service.findAll()
                .map(list -> PageResponse.fromList(list, page, size))
                .map(ResponseEntity::ok)
                .onErrorResume(e -> Mono.just(ResponseEntity.badRequest().build()));
    }

    @Operation(summary = "根据 ID 查询 InvestmentAsset")
    @GetMapping("/{id}")
    public Mono<ResponseEntity<InvestmentAssetDto>> get(@PathVariable Long id) {
        return service.findById(id).map(ResponseEntity::ok)
        .onErrorResume(e ->
        Mono.just(ResponseEntity.badRequest().build()));
    }

    @Operation(summary = "创建 InvestmentAsset")
    @PostMapping
    public Mono<ResponseEntity<InvestmentAssetDto>> create(@RequestBody Mono<InvestmentAssetDto> dto) {
        return service.save(dto).map(ResponseEntity::ok)
                .onErrorResume(e ->
                Mono.just(ResponseEntity.badRequest().build()));
    }

    @Operation(summary = "更新 InvestmentAsset")
    @PutMapping
    public Mono<ResponseEntity<InvestmentAssetDto>> update(@RequestBody Mono<InvestmentAssetDto> dto) {
        return service.save(dto).map(ResponseEntity::ok)
                    .onErrorResume(e ->
                    Mono.just(ResponseEntity.badRequest().build()));
    }

    @Operation(summary = "删除 InvestmentAsset")
    @DeleteMapping("/{id}")
    public Mono<Void> delete(@PathVariable Long id) {
        return service.deleteById(id);
    }
}
