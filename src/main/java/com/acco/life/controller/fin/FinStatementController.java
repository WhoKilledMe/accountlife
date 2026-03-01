package com.acco.life.controller.fin;

import com.acco.life.common.PageResponse;
import com.acco.life.dto.fin.FinStatementDto;
import com.acco.life.dto.fin.FinStatementFileDto;
import com.acco.life.enums.TransactionSourceType;
import com.acco.life.service.fin.FinStatementService;
import com.acco.life.util.UserUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 账单管理 Controller
 *
 * @author wensen.zhang
 * @version V2.0.0
 */
@Slf4j
@RestController
@RequestMapping("/api/fin/statement")
@RequiredArgsConstructor
public class FinStatementController {

    private final FinStatementService statementService;

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Mono<ResponseEntity<List<FinStatementFileDto>>> upload(
            @RequestPart("files") Flux<FilePart> filesFlux,
            @RequestParam("type") TransactionSourceType type,
            @RequestParam(required = false, defaultValue = "FILE_UPLOAD") String sourceChannel,
            @RequestParam(required = false, defaultValue = "false") Boolean autoSync) {
        
        return UserUtil.getCurrentUserId()
                .flatMap(userId -> filesFlux
                        .flatMap(filePart -> statementService.uploadFile(userId, filePart, type, sourceChannel, autoSync))
                        .collectList()
                        .map(ResponseEntity::ok));
    }

    /**
     * 分页查询账单文件
     */
    @PostMapping("/file/page")
    public Mono<ResponseEntity<PageResponse<FinStatementFileDto>>> pageFiles(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String platformCode,
            @RequestParam(required = false) String status) {
        return UserUtil.getCurrentUserId()
                .flatMap(userId -> statementService.pageFiles(userId, platformCode, status, page, size))
                .map(ResponseEntity::ok);
    }

    /**
     * 根据文件ID查询账单行
     */
    @GetMapping("/file/{fileId}/statements")
    public Mono<ResponseEntity<List<FinStatementDto>>> findStatementsByFileId(@PathVariable Long fileId) {
        return statementService.findStatementsByFileId(fileId)
                .map(ResponseEntity::ok);
    }

    /**
     * 分页查询账单行
     */
    @PostMapping("/page")
    public Mono<ResponseEntity<PageResponse<FinStatementDto>>> pageStatements(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) Long fileId,
            @RequestParam(required = false) String platformCode,
            @RequestParam(required = false) String status) {
        return UserUtil.getCurrentUserId()
                .flatMap(userId -> statementService.pageStatements(userId, fileId, platformCode, status, page, size))
                .map(ResponseEntity::ok);
    }

    /**
     * 根据时间范围查询账单行
     */
    @GetMapping("/range")
    public Mono<ResponseEntity<List<FinStatementDto>>> findByTimeRange(
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime startTime,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime endTime) {
        return UserUtil.getCurrentUserId()
                .flatMap(userId -> statementService.findStatementsByUserIdAndTimeRange(userId, startTime, endTime))
                .map(ResponseEntity::ok);
    }

    /**
     * 查询待对账的账单
     */
    @GetMapping("/pending")
    public Mono<ResponseEntity<List<FinStatementDto>>> findPendingReconciliation() {
        return UserUtil.getCurrentUserId()
                .flatMap(statementService::findPendingReconciliation)
                .map(ResponseEntity::ok);
    }

    /**
     * 忽略账单行
     */
    @PostMapping("/{id}/ignore")
    public Mono<ResponseEntity<Void>> ignoreStatement(@PathVariable Long id) {
        return statementService.ignoreStatement(id)
                .then(Mono.just(ResponseEntity.ok().<Void>build()));
    }

    /**
     * 批量忽略账单行
     */
    @PostMapping("/ignore-batch")
    public Mono<ResponseEntity<Void>> ignoreStatements(@RequestBody List<Long> ids) {
        return statementService.ignoreStatements(ids)
                .then(Mono.just(ResponseEntity.ok().<Void>build()));
    }
}
