package com.acco.life.controller;

import com.acco.life.dto.PlatformTransactionDto;
import com.acco.life.service.PlatformTransactionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Tag(name = "PlatformTransaction 接口")
@RestController
@RequestMapping("/api/platformtransaction")
@RequiredArgsConstructor
public class PlatformTransactionController {

    private final PlatformTransactionService service;

    @Operation(summary = "查询所有 PlatformTransaction")
    @GetMapping
    public Flux<PlatformTransactionDto> list() {
        return service.findAll();
    }

    @Operation(summary = "根据 ID 查询 PlatformTransaction")
    @GetMapping("/{id}")
    public Mono<PlatformTransactionDto> get(@PathVariable Integer id) {
        return service.findById(id);
    }

    @Operation(summary = "创建 PlatformTransaction")
    @PostMapping
    public Mono<PlatformTransactionDto> create(@RequestBody Mono<PlatformTransactionDto> dto) {
        return service.save(dto);
    }

    @Operation(summary = "更新 PlatformTransaction")
    @PutMapping
    public Mono<PlatformTransactionDto> update(@RequestBody Mono<PlatformTransactionDto> dto) {
        return service.save(dto);
    }

    @Operation(summary = "删除 PlatformTransaction")
    @DeleteMapping("/{id}")
    public Mono<Void> delete(@PathVariable Integer id) {
        return service.deleteById(id);
    }
}
