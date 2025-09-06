package com.acco.life.controller;

import com.acco.life.dto.AssetAccountDto;
import com.acco.life.common.PageResponse;
import com.acco.life.service.AssetAccountService;
import com.acco.life.util.UserIdInjectorUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.stream.Collectors;

/**
 * description: 资产账户控制器，提供资产账户的增删改查接口
 *
 * @date: 2025-07-24 17:54:23
 * @author wensen.zhang
 * @version V1.0.0
 */
@Tag(name = "AssetAccount 接口")
@RestController
@RequestMapping("/api/assetaccount")
@RequiredArgsConstructor
public class AssetAccountController {

    private final AssetAccountService service;

    @Operation(summary = "查询所有 AssetAccount")
    @GetMapping
    public Mono<ResponseEntity<List<AssetAccountDto>>> list() {
        return service.findAll().map(ResponseEntity::ok)
                    .onErrorResume(e ->
                    Mono.just(ResponseEntity.badRequest().build()));
    }

    @Operation(summary = "分页查询 AssetAccount（数据库分页+模糊搜索）")
    @PostMapping("/page")
    public Mono<ResponseEntity<PageResponse<AssetAccountDto>>> page(
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "10") int size,
            @RequestBody(required = false) AssetAccountDto filter
    ) {
        return service.page(filter, page, size)
                .map(ResponseEntity::ok)
                .onErrorResume(e -> Mono.just(ResponseEntity.badRequest().build()));
    }

    @Operation(summary = "根据 ID 查询 AssetAccount")
    @GetMapping("/{id}")
    public Mono<ResponseEntity<AssetAccountDto>> get(@PathVariable Long  id) {
        return service.findById(id).map(ResponseEntity::ok)
        .onErrorResume(e ->
        Mono.just(ResponseEntity.badRequest().build()));
    }

    @Operation(summary = "创建 AssetAccount")
    @PostMapping
    public Mono<ResponseEntity<AssetAccountDto>> create(@RequestBody Mono<AssetAccountDto> dto) {
        return UserIdInjectorUtil.withUserId(dto)
                .flatMap(d -> service.save(Mono.just(d)))
                .map(ResponseEntity::ok)
                .onErrorResume(e ->
                        Mono.just(ResponseEntity.badRequest().build()));
    }

    @Operation(summary = "更新 AssetAccount")
    @PutMapping
    public Mono<ResponseEntity<AssetAccountDto>> update(@RequestBody Mono<AssetAccountDto> dto) {
        return UserIdInjectorUtil.withUserId(dto)
                .flatMap(d -> service.save(Mono.just(d)))
                .map(ResponseEntity::ok)
                .onErrorResume(e ->
                        Mono.just(ResponseEntity.badRequest().build()));
    }

    @Operation(summary = "删除 AssetAccount")
    @DeleteMapping("/{id}")
    public Mono<Void> delete(@PathVariable Long  id) {
        return service.deleteById(id);
    }

    @Operation(summary = "下拉选择-分页查询 AssetAccount（支持名称模糊查询）")
    @GetMapping("/select")
    public Mono<ResponseEntity<PageResponse<AssetAccountDto>>> select(
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "10") int size,
            @RequestParam(name = "accountName", required = false) String accountName
    ) {
        return service.findAll()
                .map(list -> {
                    List<AssetAccountDto> filtered = list;
                    if (accountName != null && !accountName.isEmpty()) {
                        filtered = list.stream()
                                .filter(a -> a.getName() != null && a.getName().toLowerCase().contains(accountName.toLowerCase()))
                                .collect(Collectors.toList());
                    }
                    return PageResponse.fromList(filtered, page, size);
                })
                .map(ResponseEntity::ok)
                .onErrorResume(e -> Mono.just(ResponseEntity.badRequest().build()));
    }
}