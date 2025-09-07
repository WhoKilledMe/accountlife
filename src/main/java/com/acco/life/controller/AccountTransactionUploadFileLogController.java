package com.acco.life.controller;

import com.acco.life.common.PageResponse;
import com.acco.life.dto.AccountTransactionUploadFileLogDto;
import com.acco.life.service.AccountTransactionUploadFileLogService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.List;

@Tag(name = "AccountTransactionUploadFileLog 接口")
@RestController
@RequestMapping("/api/accounttransaction/uploadlog")
@RequiredArgsConstructor
public class AccountTransactionUploadFileLogController {

    private final AccountTransactionUploadFileLogService service;
    private static final Logger log = LoggerFactory.getLogger(AccountTransactionUploadFileLogController.class);

    @Operation(summary = "查询所有上传文件日志")
    @GetMapping
    public Mono<ResponseEntity<List<AccountTransactionUploadFileLogDto>>> list() {
        return service.findAll().map(ResponseEntity::ok)
                .onErrorResume(e -> {
                    log.error("list upload logs failed", e);
                    return Mono.just(ResponseEntity.badRequest().build());
                });
    }

    @Operation(summary = "分页查询上传文件日志")
    @PostMapping("/page")
    public Mono<ResponseEntity<PageResponse<AccountTransactionUploadFileLogDto>>> page(
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "10") int size,
            @RequestBody(required = false) AccountTransactionUploadFileLogDto filter
    ) {
        return service.page(filter, page, size)
                .map(ResponseEntity::ok)
                .onErrorResume(e -> {
                    log.error("page upload logs failed", e);
                    return Mono.just(ResponseEntity.badRequest().build());
                });
    }

    @Operation(summary = "根据ID查询上传文件日志")
    @GetMapping("/{id}")
    public Mono<ResponseEntity<AccountTransactionUploadFileLogDto>> get(@PathVariable Long id) {
        return service.findById(id).map(ResponseEntity::ok)
                .onErrorResume(e -> {
                    log.error("get upload log failed, id={}", id, e);
                    return Mono.just(ResponseEntity.badRequest().build());
                });
    }

    @Operation(summary = "创建上传文件日志")
    @PostMapping
    public Mono<ResponseEntity<AccountTransactionUploadFileLogDto>> create(@RequestBody Mono<AccountTransactionUploadFileLogDto> dto) {
        return service.save(dto).map(ResponseEntity::ok)
                .onErrorResume(e -> {
                    log.error("create upload log failed", e);
                    return Mono.just(ResponseEntity.badRequest().build());
                });
    }

    @Operation(summary = "更新上传文件日志")
    @PutMapping
    public Mono<ResponseEntity<AccountTransactionUploadFileLogDto>> update(@RequestBody Mono<AccountTransactionUploadFileLogDto> dto) {
        return service.save(dto).map(ResponseEntity::ok)
                .onErrorResume(e -> {
                    log.error("update upload log failed", e);
                    return Mono.just(ResponseEntity.badRequest().build());
                });
    }

    @Operation(summary = "删除上传文件日志")
    @DeleteMapping("/{id}")
    public Mono<Void> delete(@PathVariable Long id) {
        return service.deleteById(id);
    }
}


