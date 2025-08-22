package com.acco.life.controller;

import com.acco.life.enums.TransactionSourceType;
import com.acco.life.service.TransactionCsvParseService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

/**
 * description: 文件上传控制器，处理文件上传及解析相关接口
 *
 * @date: 2025-07-24 17:54:23
 * @author wensen.zhang
 * @version V1.0.0
 */
@RestController
@RequestMapping("/v1/file")
@RequiredArgsConstructor
@Slf4j
public class FileController {

    private final TransactionCsvParseService transactionCsvParseService;
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Mono<ResponseEntity<Void>> upload(
            @RequestPart("file") Mono<FilePart> fileMono,
            @RequestParam("type") TransactionSourceType type,
            @RequestParam("accountName") String accountName
    ) {
        return fileMono.flatMap(filePart -> {
            Path tempFile = Paths.get(System.getProperty("java.io.tmpdir"), UUID.randomUUID() + "-" + filePart.filename());
            return filePart.transferTo(tempFile)
                    .then(Mono.using(
                            () -> Files.newInputStream(tempFile),
                            is -> {
                                log.info("开始解析CSV文件，类型: {}, 账户名称: {}", type, accountName);
                                // 直接调用并订阅响应式流
                                return transactionCsvParseService.parseCsvFile(is, type, accountName)
                                        .then(Mono.just(ResponseEntity.ok().<Void>build()))
                                        .doOnSuccess(result -> log.info("CSV文件解析完成"))
                                        .doOnError(error -> log.error("CSV文件解析失败", error));
                            },
                            is -> {
                                try { 
                                    Files.deleteIfExists(tempFile); 
                                    log.debug("临时文件已删除: {}", tempFile);
                                } catch (Exception e) { 
                                    log.warn("删除临时文件失败: {}", tempFile, e);
                                }
                            }
                    ));
        }).onErrorResume(e -> {
            log.error("文件处理失败", e);
            return Mono.just(ResponseEntity.badRequest().build());
        });
    }

}