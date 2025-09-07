package com.acco.life.controller;

import com.acco.life.dto.BusinessTransactionDto;
import com.acco.life.common.PageResponse;
import com.acco.life.service.BusinessTransactionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * description: 业务交易控制器，提供业务交易的增删改查接口
 *
 * @date: 2025-01-15 10:00:00
 * @author wensen.zhang
 * @version V1.0.0
 */

@Tag(name = "BusinessTransaction 接口")
@RestController
@RequestMapping("/api/businesstransaction")
@RequiredArgsConstructor
public class BusinessTransactionController {

    private final BusinessTransactionService service;

    @Operation(summary = "查询所有 BusinessTransaction")
    @GetMapping
    public Mono<ResponseEntity<List<BusinessTransactionDto>>> list() {
        return service.findAll().map(ResponseEntity::ok)
                    .onErrorResume(_ ->
                    Mono.just(ResponseEntity.badRequest().build()));
    }

    @Operation(summary = "分页查询 BusinessTransaction（数据库分页+模糊搜索）")
    @PostMapping("/page")
    public Mono<ResponseEntity<PageResponse<BusinessTransactionDto>>> page(
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "10") int size,
            @RequestBody(required = false) BusinessTransactionDto filter
    ) {
        return service.page(filter, page, size)
                .map(ResponseEntity::ok)
                .onErrorResume(_ -> Mono.just(ResponseEntity.badRequest().build()));
    }

    @Operation(summary = "根据 ID 查询 BusinessTransaction")
    @GetMapping("/{id}")
    public Mono<ResponseEntity<BusinessTransactionDto>> get(@PathVariable Long id) {
        return service.findById(id).map(ResponseEntity::ok)
        .onErrorResume(_ ->
        Mono.just(ResponseEntity.badRequest().build()));
    }

    @Operation(summary = "创建 BusinessTransaction")
    @PostMapping
    public Mono<ResponseEntity<BusinessTransactionDto>> create(@RequestBody Mono<BusinessTransactionDto> dto) {
        return service.save(dto).map(ResponseEntity::ok)
                .onErrorResume(_ ->
                Mono.just(ResponseEntity.badRequest().build()));
    }

    @Operation(summary = "更新 BusinessTransaction")
    @PutMapping
    public Mono<ResponseEntity<BusinessTransactionDto>> update(@RequestBody Mono<BusinessTransactionDto> dto) {
        return service.save(dto).map(ResponseEntity::ok)
                    .onErrorResume(_ ->
                    Mono.just(ResponseEntity.badRequest().build()));
    }

    @Operation(summary = "删除 BusinessTransaction")
    @DeleteMapping("/{id}")
    public Mono<Void> delete(@PathVariable Long id) {
        return service.deleteById(id);
    }
}
