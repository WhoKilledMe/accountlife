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
            @RequestParam("type") TransactionSourceType type
    ) {
        return fileMono.flatMap(filePart -> {
            Path tempFile = Paths.get(System.getProperty("java.io.tmpdir"), UUID.randomUUID() + "-" + filePart.filename());
            return filePart.transferTo(tempFile)
                    .then(Mono.using(
                            () -> Files.newInputStream(tempFile),
                            is -> {
                                transactionCsvParseService.parseCsvFile(is, type);
                                return Mono.just(ResponseEntity.ok().<Void>build());
                            }
                    ));
        }).onErrorResume(e -> {
            log.error("文件处理失败", e);
            return Mono.just(ResponseEntity.badRequest().build());
        });
    }

}
