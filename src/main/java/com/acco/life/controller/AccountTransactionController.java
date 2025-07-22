package com.acco.life.controller;

import com.acco.life.dto.AccountTransactionDto;
import com.acco.life.service.AccountTransactionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.List;

@Tag(name = "AccountTransaction 接口")
@RestController
@RequestMapping("/api/accounttransaction")
@RequiredArgsConstructor
public class AccountTransactionController {

    private final AccountTransactionService service;

    @Operation(summary = "查询所有 AccountTransaction")
    @GetMapping
    public Mono<ResponseEntity<List<AccountTransactionDto>>> list() {
        return service.findAll().map(ResponseEntity::ok)
                    .onErrorResume(e ->
                    Mono.just(ResponseEntity.badRequest().build()));
    }

    @Operation(summary = "根据 ID 查询 AccountTransaction")
    @GetMapping("/{id}")
    public Mono<ResponseEntity<AccountTransactionDto>> get(@PathVariable Integer id) {
        return service.findById(id).map(ResponseEntity::ok)
        .onErrorResume(e ->
        Mono.just(ResponseEntity.badRequest().build()));
    }

    @Operation(summary = "创建 AccountTransaction")
    @PostMapping
    public Mono<ResponseEntity<AccountTransactionDto>> create(@RequestBody Mono<AccountTransactionDto> dto) {
        return service.save(dto).map(ResponseEntity::ok)
                .onErrorResume(e ->
                Mono.just(ResponseEntity.badRequest().build()));
    }

    @Operation(summary = "更新 AccountTransaction")
    @PutMapping
    public Mono<ResponseEntity<AccountTransactionDto>> update(@RequestBody Mono<AccountTransactionDto> dto) {
        return service.save(dto).map(ResponseEntity::ok)
                    .onErrorResume(e ->
                    Mono.just(ResponseEntity.badRequest().build()));
    }

    @Operation(summary = "删除 AccountTransaction")
    @DeleteMapping("/{id}")
    public Mono<Void> delete(@PathVariable Integer id) {
        return service.deleteById(id);
    }
}
