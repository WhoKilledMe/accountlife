package com.acco.life.controller.fin;

import com.acco.life.common.PageResponse;
import com.acco.life.dto.fin.FinStatementDto;
import com.acco.life.dto.fin.FinStatementFileDto;
import com.acco.life.entity.fin.FinStatement;
import com.acco.life.entity.fin.FinStatementFile;
import com.acco.life.enums.TransactionSourceType;
import com.acco.life.repository.fin.FinStatementFileRepository;
import com.acco.life.repository.fin.FinStatementRepository;
import com.acco.life.service.csv.FinStatementCsvParser;
import com.acco.life.service.fin.FinStatementService;
import com.acco.life.util.UserUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

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
    private final FinStatementFileRepository statementFileRepository;
    private final FinStatementRepository statementRepository;
    private final List<FinStatementCsvParser> csvParsers;

    /**
     * 上传并解析账单文件
     */
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Mono<ResponseEntity<FinStatementFileDto>> upload(
            @RequestPart("file") Mono<FilePart> fileMono,
            @RequestParam("type") TransactionSourceType type,
            @RequestParam(required = false, defaultValue = "FILE_UPLOAD") String sourceChannel) {
        
        return fileMono.flatMap(filePart -> {
            Path tempFile = Paths.get(System.getProperty("java.io.tmpdir"), UUID.randomUUID() + "-" + filePart.filename());
            
            return filePart.transferTo(tempFile)
                    .then(Mono.defer(() -> {
                        try {
                            // 计算文件 MD5
                            byte[] fileBytes = Files.readAllBytes(tempFile);
                            String fileMd5 = DigestUtils.md5DigestAsHex(fileBytes);
                            
                            // 获取对应的解析器
                            FinStatementCsvParser parser = csvParsers.stream()
                                    .filter(p -> p.supports(type))
                                    .findFirst()
                                    .orElseThrow(() -> new IllegalArgumentException("未找到匹配的解析器: " + type));
                            
                            return UserUtil.getCurrentUserId()
                                    .flatMap(userId -> {
                                        // 检查文件是否已导入
                                        return statementService.isFileImported(fileMd5)
                                                .flatMap(imported -> {
                                                    if (imported) {
                                                        return Mono.error(new IllegalStateException("文件已导入"));
                                                    }
                                                    
                                                    // 创建文件记录
                                                    FinStatementFile fileRecord = new FinStatementFile();
                                                    fileRecord.setUserId(userId);
                                                    fileRecord.setPlatformCode(parser.getPlatformCode());
                                                    fileRecord.setFileName(filePart.filename());
                                                    fileRecord.setFileMd5(fileMd5);
                                                    fileRecord.setFileType("CSV");
                                                    fileRecord.setSourceChannel(sourceChannel);
                                                    fileRecord.setStatus("PROCESSING");
                                                    fileRecord.setUploadedAt(LocalDateTime.now());
                                                    
                                                    return statementFileRepository.save(fileRecord)
                                                            .flatMap(savedFile -> {
                                                                try {
                                                                    // 解析文件
                                                                    var dtos = parser.parse(Files.newInputStream(tempFile));
                                                                    var statements = parser.buildStatements(dtos, userId, savedFile.getId());
                                                                    
                                                                    // 保存账单行
                                                                    return statementRepository.saveAll(statements)
                                                                            .collectList()
                                                                            .flatMap(savedStatements -> {
                                                                                // 更新文件记录
                                                                                savedFile.setTotalRows(dtos.size());
                                                                                savedFile.setSuccessCount(savedStatements.size());
                                                                                savedFile.setFailureCount(dtos.size() - savedStatements.size());
                                                                                savedFile.setStatus("COMPLETED");
                                                                                savedFile.setProcessedAt(LocalDateTime.now());
                                                                                
                                                                                return statementFileRepository.save(savedFile);
                                                                            });
                                                                } catch (Exception e) {
                                                                    log.error("解析文件失败", e);
                                                                    savedFile.setStatus("FAILED");
                                                                    savedFile.setErrorLog(e.getMessage());
                                                                    return statementFileRepository.save(savedFile);
                                                                }
                                                            });
                                                });
                                    });
                        } catch (Exception e) {
                            return Mono.error(e);
                        }
                    }))
                    .map(file -> {
                        FinStatementFileDto dto = new FinStatementFileDto();
                        dto.setId(file.getId());
                        dto.setFileName(file.getFileName());
                        dto.setStatus(file.getStatus());
                        dto.setTotalRows(file.getTotalRows());
                        dto.setSuccessCount(file.getSuccessCount());
                        dto.setFailureCount(file.getFailureCount());
                        return ResponseEntity.ok(dto);
                    })
                    .doFinally(signal -> {
                        try {
                            Files.deleteIfExists(tempFile);
                        } catch (Exception e) {
                            log.warn("删除临时文件失败", e);
                        }
                    });
        });
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
