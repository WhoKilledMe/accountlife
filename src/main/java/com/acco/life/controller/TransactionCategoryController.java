package com.acco.life.controller;

import com.acco.life.dto.TransactionCategoryDto;
import com.acco.life.service.TransactionCategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * description: 交易分类控制器，提供交易分类的增删改查接口
 *
 * @date: 2025-07-24 17:54:23
 * @author wensen.zhang
 * @version V1.0.0
 */

@Tag(name = "TransactionCategory 接口")
@RestController
@RequestMapping("/api/transactioncategory")
@RequiredArgsConstructor
public class TransactionCategoryController {

    private final TransactionCategoryService service;

    @Operation(summary = "查询所有 TransactionCategory")
    @GetMapping
    public Mono<ResponseEntity<List<TransactionCategoryDto>>> list() {
        return service.findAll().map(ResponseEntity::ok)
                    .onErrorResume(e ->
                    Mono.just(ResponseEntity.badRequest().build()));
    }

    @Operation(summary = "以树形结构查询所有 TransactionCategory")
    @GetMapping("/tree")
    public Mono<ResponseEntity<List<TransactionCategoryDto>>> listTree() {
        return service.findTree().map(ResponseEntity::ok)
                .onErrorResume(e -> Mono.just(ResponseEntity.badRequest().build()));
    }

    @Operation(summary = "根据 ID 查询 TransactionCategory")
    @GetMapping("/{id}")
    public Mono<ResponseEntity<TransactionCategoryDto>> get(@PathVariable Integer id) {
        return service.findById(id).map(ResponseEntity::ok)
        .onErrorResume(e ->
        Mono.just(ResponseEntity.badRequest().build()));
    }

    @Operation(summary = "创建 TransactionCategory")
    @PostMapping
    public Mono<ResponseEntity<TransactionCategoryDto>> create(@RequestBody Mono<TransactionCategoryDto> dto) {
        return service.save(dto).map(ResponseEntity::ok)
                .onErrorResume(e ->
                Mono.just(ResponseEntity.badRequest().build()));
    }

    @Operation(summary = "更新 TransactionCategory")
    @PutMapping
    public Mono<ResponseEntity<TransactionCategoryDto>> update(@RequestBody Mono<TransactionCategoryDto> dto) {
        return service.save(dto).map(ResponseEntity::ok)
                    .onErrorResume(e ->
                    Mono.just(ResponseEntity.badRequest().build()));
    }

    @Operation(summary = "删除 TransactionCategory")
    @DeleteMapping("/{id}")
    public Mono<Void> delete(@PathVariable Integer id) {
        return service.deleteById(id);
    }
}
