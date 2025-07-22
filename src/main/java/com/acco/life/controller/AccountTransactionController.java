package com.acco.life.controller;

import com.acco.life.dto.AccountTransactionDto;
import com.acco.life.service.AccountTransactionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Tag(name = "AccountTransaction 接口")
@RestController
@RequestMapping("/api/accounttransaction")
@RequiredArgsConstructor
public class AccountTransactionController {

    private final AccountTransactionService service;

    @Operation(summary = "查询所有 AccountTransaction")
    @GetMapping
    public Flux<AccountTransactionDto> list() {
        return service.findAll();
    }

    @Operation(summary = "根据 ID 查询 AccountTransaction")
    @GetMapping("/{id}")
    public Mono<AccountTransactionDto> get(@PathVariable Integer id) {
        return service.findById(id);
    }

    @Operation(summary = "创建 AccountTransaction")
    @PostMapping
    public Mono<AccountTransactionDto> create(@RequestBody Mono<AccountTransactionDto> dto) {
        return service.save(dto);
    }

    @Operation(summary = "更新 AccountTransaction")
    @PutMapping
    public Mono<AccountTransactionDto> update(@RequestBody Mono<AccountTransactionDto> dto) {
        return service.save(dto);
    }

    @Operation(summary = "删除 AccountTransaction")
    @DeleteMapping("/{id}")
    public Mono<Void> delete(@PathVariable Integer id) {
        return service.deleteById(id);
    }
}
