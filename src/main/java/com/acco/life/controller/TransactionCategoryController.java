package com.acco.life.controller;

import com.acco.life.dto.TransactionCategoryDto;
import com.acco.life.service.TransactionCategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Tag(name = "TransactionCategory 接口")
@RestController
@RequestMapping("/api/transactioncategory")
@RequiredArgsConstructor
public class TransactionCategoryController {

    private final TransactionCategoryService service;

    @Operation(summary = "查询所有 TransactionCategory")
    @GetMapping
    public Flux<TransactionCategoryDto> list() {
        return service.findAll();
    }

    @Operation(summary = "根据 ID 查询 TransactionCategory")
    @GetMapping("/{id}")
    public Mono<TransactionCategoryDto> get(@PathVariable Integer id) {
        return service.findById(id);
    }

    @Operation(summary = "创建 TransactionCategory")
    @PostMapping
    public Mono<TransactionCategoryDto> create(@RequestBody Mono<TransactionCategoryDto> dto) {
        return service.save(dto);
    }

    @Operation(summary = "更新 TransactionCategory")
    @PutMapping
    public Mono<TransactionCategoryDto> update(@RequestBody Mono<TransactionCategoryDto> dto) {
        return service.save(dto);
    }

    @Operation(summary = "删除 TransactionCategory")
    @DeleteMapping("/{id}")
    public Mono<Void> delete(@PathVariable Integer id) {
        return service.deleteById(id);
    }
}
