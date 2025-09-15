package com.acco.life.controller;

import com.acco.life.common.PageResponse;
import com.acco.life.dto.AccountTransactionUploadFileLogDto;
import com.acco.life.service.AccountTransactionUploadFileLogService;
import com.acco.life.service.TransactionCsvParseService;
import com.acco.life.enums.TransactionSourceType;
import com.acco.life.util.ZipUtil;
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
    private final TransactionCsvParseService transactionCsvParseService;
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

    @Operation(summary = "根据日志ID进行附件同步（邮箱）")
    @PostMapping("/{id}/sync-mail")
    public Mono<ResponseEntity<String>> syncByLogId(@PathVariable Long id) {
        return service.findById(id)
                .switchIfEmpty(Mono.error(new IllegalArgumentException("未找到文件日志: " + id)))
                .flatMap(log -> {
                    // 更新为处理中
                    log.setStatus("PROCESSING");
                    log.setUploadType(2);
                    return service.save(Mono.just(log));
                })
                .flatMap(log -> Mono.fromCallable(() -> {
                    java.io.File zipFile = new java.io.File(log.getFilePath());
                    java.io.File destDir = zipFile.getParentFile();
                    String password = log.getZipPassword() == null ? null : String.valueOf(log.getZipPassword());
                    try {
                        java.util.List<String> extractedPaths;
                        if (password != null) {
                            extractedPaths = ZipUtil.unzipWithPassword(zipFile, destDir, password);
                        } else {
                            // 简单穷举尝试（可能耗时，示例）
                            extractedPaths = null;
                            for (String candidate : ZipUtil.randomDigit()) {
                                try {
                                    extractedPaths = ZipUtil.unzipWithPassword(zipFile, destDir, candidate);
                                    password = candidate;
                                    break;
                                } catch (Exception ignored) {}
                            }
                            if (extractedPaths == null) {
                                throw new IllegalStateException("无法解压ZIP，未找到有效密码");
                            }
                        }
                        return new java.util.AbstractMap.SimpleEntry<>(log, extractedPaths);
                    } catch (Exception e) {
                        throw new RuntimeException(e.getMessage(), e);
                    }
                }))
                .flatMap(entry -> {
                    AccountTransactionUploadFileLogDto log = entry.getKey();
                    java.util.List<String> extractedPaths = entry.getValue();
                    // 选择美团CSV文件，或回退到第一个
                    String csvPath = extractedPaths.stream()
                            .filter(p -> p.endsWith(".csv"))
                            .findFirst()
                            .orElseGet(() -> extractedPaths.isEmpty() ? null : extractedPaths.get(0));
                    if (csvPath == null) {
                        return Mono.error(new IllegalStateException("未找到解压后的CSV文件"));
                    }
                    java.io.File extracted = new java.io.File(csvPath);
                    return Mono.just(extracted).flatMap(file -> {
                    try {
                        java.io.InputStream in = new java.io.FileInputStream(file);
                        String accountName = log.getAccountName() != null ? log.getAccountName() : "";
                        return transactionCsvParseService.parseCsvFile(in, TransactionSourceType.PLATFORM, accountName)
                                .thenReturn(new java.util.AbstractMap.SimpleEntry<>(log, csvPath));
                    } catch (java.io.FileNotFoundException e) {
                        return Mono.error(e);
                    }
                });
                })
                .flatMap(entry -> {
                    AccountTransactionUploadFileLogDto log = entry.getKey();
                    String csvPath = entry.getValue();
                    log.setStatus("COMPLETED");
                    log.setFilePath(csvPath);
                    return service.save(Mono.just(log));
                })
                .map(l -> ResponseEntity.ok("同步完成"))
                .onErrorResume(e -> service.findById(id)
                        .flatMap(log -> {
                            log.setStatus("FAILED");
                            log.setErrorMessage(e.getMessage());
                            return service.save(Mono.just(log));
                        })
                        .thenReturn(ResponseEntity.badRequest().body(e.getMessage())));
    }
}


