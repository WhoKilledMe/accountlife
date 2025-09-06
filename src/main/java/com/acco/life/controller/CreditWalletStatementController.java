package com.acco.life.controller;

import com.acco.life.dto.CreditWalletStatementDto;
import com.acco.life.common.PageResponse;
import com.acco.life.service.CreditWalletStatementService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * description: 信用钱包账单控制器，提供信用钱包账单的增删改查接口
 *
 * @date: 2025-07-24 17:54:23
 * @author wensen.zhang
 * @version V1.0.0
 */

@Tag(name = "CreditWalletStatement 接口")
@RestController
@RequestMapping("/api/creditwalletstatement")
@RequiredArgsConstructor
public class CreditWalletStatementController {

    private final CreditWalletStatementService service;

    @Operation(summary = "查询所有 CreditWalletStatement")
    @GetMapping
    public Mono<ResponseEntity<List<CreditWalletStatementDto>>> list() {
        return service.findAll().map(ResponseEntity::ok)
                    .onErrorResume(e ->
                    Mono.just(ResponseEntity.badRequest().build()));
    }

    @Operation(summary = "分页查询 CreditWalletStatement")
    @GetMapping("/page")
    public Mono<ResponseEntity<PageResponse<CreditWalletStatementDto>>> page(
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "10") int size
    ) {
        return service.findAll()
                .map(list -> PageResponse.fromList(list, page, size))
                .map(ResponseEntity::ok)
                .onErrorResume(e -> Mono.just(ResponseEntity.badRequest().build()));
    }

    @Operation(summary = "根据 ID 查询 CreditWalletStatement")
    @GetMapping("/{id}")
    public Mono<ResponseEntity<CreditWalletStatementDto>> get(@PathVariable Long  id) {
        return service.findById(id).map(ResponseEntity::ok)
        .onErrorResume(e ->
        Mono.just(ResponseEntity.badRequest().build()));
    }

    @Operation(summary = "创建 CreditWalletStatement")
    @PostMapping
    public Mono<ResponseEntity<CreditWalletStatementDto>> create(@RequestBody Mono<CreditWalletStatementDto> dto) {
        return service.save(dto).map(ResponseEntity::ok)
                .onErrorResume(e ->
                Mono.just(ResponseEntity.badRequest().build()));
    }

    @Operation(summary = "更新 CreditWalletStatement")
    @PutMapping
    public Mono<ResponseEntity<CreditWalletStatementDto>> update(@RequestBody Mono<CreditWalletStatementDto> dto) {
        return service.save(dto).map(ResponseEntity::ok)
                    .onErrorResume(e ->
                    Mono.just(ResponseEntity.badRequest().build()));
    }

    @Operation(summary = "删除 CreditWalletStatement")
    @DeleteMapping("/{id}")
    public Mono<Void> delete(@PathVariable Long  id) {
        return service.deleteById(id);
    }
}
