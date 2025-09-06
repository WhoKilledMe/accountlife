package com.acco.life.controller;

import com.acco.life.dto.PlatformTransactionDto;
import com.acco.life.common.PageResponse;
import com.acco.life.service.PlatformTransactionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * description: 平台交易控制器，提供平台交易的增删改查接口
 *
 * @date: 2025-07-24 17:54:23
 * @author wensen.zhang
 * @version V1.0.0
 */
@Tag(name = "PlatformTransaction 接口")
@RestController
@RequestMapping("/api/platformtransaction")
@RequiredArgsConstructor
public class PlatformTransactionController {

    private final PlatformTransactionService service;

    @Operation(summary = "查询所有 PlatformTransaction")
    @GetMapping
    public Mono<ResponseEntity<List<PlatformTransactionDto>>> list() {
        return service.findAll().map(ResponseEntity::ok)
                    .onErrorResume(e ->
                    Mono.just(ResponseEntity.badRequest().build()));
    }

    @Operation(summary = "分页查询 PlatformTransaction")
    @GetMapping("/page")
    public Mono<ResponseEntity<PageResponse<PlatformTransactionDto>>> page(
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "10") int size
    ) {
        return service.findAll()
                .map(list -> PageResponse.fromList(list, page, size))
                .map(ResponseEntity::ok)
                .onErrorResume(e -> Mono.just(ResponseEntity.badRequest().build()));
    }

    @Operation(summary = "根据 ID 查询 PlatformTransaction")
    @GetMapping("/{id}")
    public Mono<ResponseEntity<PlatformTransactionDto>> get(@PathVariable Long id) {
        return service.findById(id).map(ResponseEntity::ok)
        .onErrorResume(e ->
        Mono.just(ResponseEntity.badRequest().build()));
    }

    @Operation(summary = "创建 PlatformTransaction")
    @PostMapping
    public Mono<ResponseEntity<PlatformTransactionDto>> create(@RequestBody Mono<PlatformTransactionDto> dto) {
        return service.save(dto).map(ResponseEntity::ok)
                .onErrorResume(e ->
                Mono.just(ResponseEntity.badRequest().build()));
    }

    @Operation(summary = "更新 PlatformTransaction")
    @PutMapping
    public Mono<ResponseEntity<PlatformTransactionDto>> update(@RequestBody Mono<PlatformTransactionDto> dto) {
        return service.save(dto).map(ResponseEntity::ok)
                    .onErrorResume(e ->
                    Mono.just(ResponseEntity.badRequest().build()));
    }

    @Operation(summary = "删除 PlatformTransaction")
    @DeleteMapping("/{id}")
    public Mono<Void> delete(@PathVariable Long id) {
        return service.deleteById(id);
    }
}
