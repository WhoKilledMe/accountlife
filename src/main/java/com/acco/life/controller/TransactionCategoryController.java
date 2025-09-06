package com.acco.life.controller;

import com.acco.life.dto.TransactionCategoryDto;
import com.acco.life.common.PageResponse;
import com.acco.life.service.TransactionCategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.stream.Collectors;

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

    @Operation(summary = "分页查询 TransactionCategory")
    @PostMapping("/page")
    public Mono<ResponseEntity<PageResponse<TransactionCategoryDto>>> page(
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "10") int size,
            @RequestBody(required = false) TransactionCategoryDto filter
    ) {
        String nameLike = filter == null ? null : filter.getName();
        return service.page(nameLike, page, size)
                .map(ResponseEntity::ok)
                .onErrorResume(e -> Mono.just(ResponseEntity.badRequest().build()));
    }

    @Operation(summary = "下拉选择-分页查询 TransactionCategory（支持名称模糊查询）")
    @GetMapping("/select")
    public Mono<ResponseEntity<PageResponse<TransactionCategoryDto>>> select(
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "10") int size,
            @RequestParam(name = "categoryName", required = false) String categoryName
    ) {
        return service.findAll()
                .map(list -> {
                    List<TransactionCategoryDto> filtered = list;
                    if (categoryName != null && !categoryName.isEmpty()) {
                        filtered = list.stream()
                                .filter(c -> c.getName() != null && c.getName().toLowerCase().contains(categoryName.toLowerCase()))
                                .collect(Collectors.toList());
                    }
                    return PageResponse.fromList(filtered, page, size);
                })
                .map(ResponseEntity::ok)
                .onErrorResume(e -> Mono.just(ResponseEntity.badRequest().build()));
    }

    @Operation(summary = "以树形结构查询所有 TransactionCategory")
    @GetMapping("/tree")
    public Mono<ResponseEntity<List<TransactionCategoryDto>>> listTree() {
        return service.findTree().map(ResponseEntity::ok)
                .onErrorResume(e -> Mono.just(ResponseEntity.badRequest().build()));
    }

    @Operation(summary = "根据 ID 查询 TransactionCategory")
    @GetMapping("/{id}")
    public Mono<ResponseEntity<TransactionCategoryDto>> get(@PathVariable Long id) {
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
    public Mono<Void> delete(@PathVariable Long id) {
        return service.deleteById(id);
    }
}
