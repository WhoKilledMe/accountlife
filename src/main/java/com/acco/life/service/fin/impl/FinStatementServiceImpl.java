package com.acco.life.service.fin.impl;

import com.acco.life.common.PageResponse;
import com.acco.life.dto.fin.FinStatementDto;
import com.acco.life.dto.fin.FinStatementFileDto;
import com.acco.life.entity.fin.FinStatement;
import com.acco.life.entity.fin.FinStatementFile;
import com.acco.life.enums.TransactionSourceType;
import com.acco.life.enums.fin.StatementStatus;
import com.acco.life.mapper.fin.FinStatementFileMapper;
import com.acco.life.mapper.fin.FinStatementMapper;
import com.acco.life.repository.fin.FinStatementFileRepository;
import com.acco.life.repository.fin.FinStatementRepository;
import com.acco.life.service.AiTransactionCategoryService;
import com.acco.life.service.csv.FinStatementCsvParser;
import com.acco.life.service.fin.FinStatementService;
import com.acco.life.service.fin.FinTransactionFlowService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * 账单导入与解析服务实现
 *
 * @author wensen.zhang
 * @version V2.0.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class FinStatementServiceImpl implements FinStatementService {

    private final FinStatementFileRepository statementFileRepository;
    private final FinStatementRepository statementRepository;
    private final FinStatementFileMapper statementFileMapper;
    private final FinStatementMapper statementMapper;
    private final List<FinStatementCsvParser> csvParsers;
    private final AiTransactionCategoryService categoryService;
    private final FinTransactionFlowService transactionFlowService;

    @Override
    public Mono<FinStatementFileDto> uploadFile(Long userId, FilePart filePart, TransactionSourceType type,
                                                 String sourceChannel, Boolean autoSync) {
        Path tempFile = createTempFile(filePart.filename());
        
        return filePart.transferTo(tempFile)
                .then(Mono.defer(() -> processUploadedFile(userId, filePart, type, sourceChannel, autoSync, tempFile)))
                .doFinally(signal -> cleanupTempFile(tempFile));
    }

    @Override
    public Mono<FinStatementFileDto> importFile(Long userId, String platformCode, String fileName, String fileMd5,
                                                 InputStream inputStream, String sourceChannel) {
        // 先检查文件是否已导入
        return isFileImported(fileMd5)
                .flatMap(imported -> {
                    if (imported) {
                        return Mono.error(new IllegalStateException("文件已导入，MD5: " + fileMd5));
                    }
                    
                    // 创建文件导入记录
                    FinStatementFile fileRecord = new FinStatementFile();
                    fileRecord.setUserId(userId);
                    fileRecord.setPlatformCode(platformCode);
                    fileRecord.setFileName(fileName);
                    fileRecord.setFileMd5(fileMd5);
                    fileRecord.setFileType(getFileType(fileName));
                    fileRecord.setSourceChannel(sourceChannel);
                    fileRecord.setStatus("PENDING");
                    fileRecord.setTotalRows(0);
                    fileRecord.setSuccessCount(0);
                    fileRecord.setFailureCount(0);
                    fileRecord.setUploadedAt(LocalDateTime.now());
                    // is_deleted 由 AuditFieldCallback 自动设置
                    if (fileRecord.getIsDeleted() == null) {
                        fileRecord.setIsDeleted(0);
                    }
                    
                    return statementFileRepository.save(fileRecord)
                            .map(statementFileMapper::toDto);
                });
    }

    @Override
    public Mono<Boolean> isFileImported(String fileMd5) {
        return statementFileRepository.findByFileMd5(fileMd5)
                .map(file -> true)
                .defaultIfEmpty(false);
    }

    @Override
    public Mono<FinStatementFileDto> findFileById(Long fileId) {
        return statementFileRepository.findById(fileId)
                .map(statementFileMapper::toDto);
    }

    @Override
    public Mono<PageResponse<FinStatementFileDto>> pageFiles(Long userId, String platformCode, String status, int page, int size) {
        int currentPage = Math.max(page, 0);
        int pageSize = Math.max(size, 1);
        long offset = (long) currentPage * pageSize;

        Mono<List<FinStatementFileDto>> dataMono = statementFileRepository.search(userId, platformCode, status, pageSize, offset)
                .map(statementFileMapper::toDto)
                .collectList();

        Mono<Long> countMono = statementFileRepository.countSearch(userId, platformCode, status);

        return Mono.zip(dataMono, countMono)
                .map(tuple -> PageResponse.of(tuple.getT1(), currentPage, pageSize, tuple.getT2()));
    }

    @Override
    public Mono<FinStatementDto> findStatementById(Long id) {
        return statementRepository.findById(id)
                .map(this::enrichDto);
    }

    @Override
    public Mono<List<FinStatementDto>> findStatementsByFileId(Long fileId) {
        return statementRepository.findByFileId(fileId)
                .map(this::enrichDto)
                .collectList();
    }

    @Override
    public Mono<List<FinStatementDto>> findStatementsByUserIdAndTimeRange(Long userId, LocalDateTime startTime, LocalDateTime endTime) {
        return statementRepository.findByUserIdAndTimeRange(userId, startTime, endTime)
                .map(this::enrichDto)
                .collectList();
    }

    @Override
    public Mono<PageResponse<FinStatementDto>> pageStatements(Long userId, Long fileId, String platformCode, String status, int page, int size) {
        int currentPage = Math.max(page, 0);
        int pageSize = Math.max(size, 1);
        long offset = (long) currentPage * pageSize;

        Mono<List<FinStatementDto>> dataMono = statementRepository.search(userId, fileId, platformCode, status, pageSize, offset)
                .map(this::enrichDto)
                .collectList();

        Mono<Long> countMono = statementRepository.countSearch(userId, fileId, platformCode, status);

        return Mono.zip(dataMono, countMono)
                .map(tuple -> PageResponse.of(tuple.getT1(), currentPage, pageSize, tuple.getT2()));
    }

    @Override
    public Mono<FinStatementDto> updateStatementStatus(Long statementId, String status) {
        return statementRepository.findById(statementId)
                .flatMap(entity -> {
                    entity.setStatus(status);
                    return statementRepository.save(entity);
                })
                .map(this::enrichDto);
    }

    @Override
    public Mono<List<FinStatementDto>> findPendingReconciliation(Long userId) {
        return statementRepository.findPendingReconciliation(userId)
                .map(this::enrichDto)
                .collectList();
    }

    @Override
    public Mono<Void> ignoreStatement(Long statementId) {
        return updateStatementStatus(statementId, StatementStatus.IGNORED.getCode())
                .then();
    }

    @Override
    public Mono<Void> ignoreStatements(List<Long> statementIds) {
        return Flux.fromIterable(statementIds)
                .flatMap(this::ignoreStatement)
                .then();
    }

    private String getFileType(String fileName) {
        if (fileName == null) {
            return "UNKNOWN";
        }
        String lowerName = fileName.toLowerCase();
        if (lowerName.endsWith(".csv")) {
            return "CSV";
        } else if (lowerName.endsWith(".zip")) {
            return "ZIP";
        } else if (lowerName.endsWith(".json")) {
            return "JSON";
        } else if (lowerName.endsWith(".pdf")) {
            return "PDF";
        }
        return "UNKNOWN";
    }

    private FinStatementDto enrichDto(FinStatement entity) {
        FinStatementDto dto = statementMapper.toDto(entity);
        
        StatementStatus status = StatementStatus.fromCode(entity.getStatus());
        if (status != null) {
            dto.setStatusName(status.getName());
        }
        
        if ("IN".equals(entity.getDirection())) {
            dto.setDirectionName("收入");
        } else if ("OUT".equals(entity.getDirection())) {
            dto.setDirectionName("支出");
        }
        
        return dto;
    }

    private Path createTempFile(String originalFilename) {
        String tempFileName = UUID.randomUUID() + "-" + originalFilename;
        return Paths.get(System.getProperty("java.io.tmpdir"), tempFileName);
    }

    private Mono<FinStatementFileDto> processUploadedFile(Long userId, FilePart filePart, TransactionSourceType type,
                                                           String sourceChannel, Boolean autoSync, Path tempFile) {
        try {
            String fileMd5 = calculateFileMd5(tempFile);
            FinStatementCsvParser parser = findParser(type);
            
            return checkFileNotImported(fileMd5)
                    .flatMap(notImported -> {
                        if (!notImported) {
                            return Mono.error(new IllegalStateException("文件已导入"));
                        }
                        return createFileRecord(userId, filePart, parser, fileMd5, sourceChannel)
                                .flatMap(savedFile -> parseAndSaveStatements(savedFile, parser, tempFile, userId, autoSync));
                    });
        } catch (Exception e) {
            log.error("处理上传文件失败", e);
            return Mono.error(e);
        }
    }

    private String calculateFileMd5(Path tempFile) {
        try {
            byte[] fileBytes = Files.readAllBytes(tempFile);
            return DigestUtils.md5DigestAsHex(fileBytes);
        } catch (Exception e) {
            log.error("计算文件MD5失败", e);
            throw new RuntimeException("计算文件MD5失败", e);
        }
    }

    private FinStatementCsvParser findParser(TransactionSourceType type) {
        return csvParsers.stream()
                .filter(p -> p.supports(type))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("未找到匹配的解析器: " + type));
    }

    private Mono<Boolean> checkFileNotImported(String fileMd5) {
        return isFileImported(fileMd5)
                .map(imported -> !imported);
    }

    private Mono<FinStatementFile> createFileRecord(Long userId, FilePart filePart, FinStatementCsvParser parser,
                                                     String fileMd5, String sourceChannel) {
        FinStatementFile fileRecord = new FinStatementFile();
        fileRecord.setUserId(userId);
        fileRecord.setPlatformCode(parser.getPlatformCode());
        fileRecord.setFileName(filePart.filename());
        fileRecord.setFileMd5(fileMd5);
        fileRecord.setFileType("CSV");
        fileRecord.setSourceChannel(sourceChannel);
        fileRecord.setStatus("PROCESSING");
        fileRecord.setUploadedAt(LocalDateTime.now());
        
        return statementFileRepository.save(fileRecord);
    }

    private Mono<FinStatementFileDto> parseAndSaveStatements(FinStatementFile savedFile, FinStatementCsvParser parser,
                                                               Path tempFile, Long userId, Boolean autoSync) {
        try {
            var dtos = parser.parse(Files.newInputStream(tempFile));
            
            return Mono.fromCallable(() -> parser.buildStatements(dtos, userId, savedFile.getId(), categoryService))
                    .subscribeOn(Schedulers.boundedElastic())
                    .flatMap(statements -> saveStatementsAndUpdateFile(savedFile, statements, dtos.size(), userId, autoSync))
                    .map(statementFileMapper::toDto);
        } catch (Exception e) {
            log.error("解析文件失败", e);
            return markFileAsFailed(savedFile, e.getMessage())
                    .map(statementFileMapper::toDto);
        }
    }

    private Mono<FinStatementFile> saveStatementsAndUpdateFile(FinStatementFile savedFile, List<FinStatement> statements,
                                                                int totalRows, Long userId, Boolean autoSync) {
        int totalSize = statements.size();
        log.info("开始批量保存账单行，总数量: {}", totalSize);
        
        return statementRepository.batchInsertIgnore(statements)
                .onErrorResume(error -> {
                    log.error("批量保存账单行失败: {}", error.getMessage(), error);
                    return Mono.just(0);
                })
                .flatMap(successCount -> {
                    updateFileRecord(savedFile, totalRows, successCount, totalSize);
                    log.info("批量保存账单行完成，总数量: {}, 成功: {}, 失败: {}", 
                            totalSize, successCount, totalSize - successCount);
                    
                    return statementFileRepository.save(savedFile)
                            .flatMap(saved -> handleAutoSync(saved, userId, autoSync));
                });
    }

    private void updateFileRecord(FinStatementFile file, int totalRows, int successCount, int totalSize) {
        file.setTotalRows(totalRows);
        file.setSuccessCount(successCount);
        file.setFailureCount(totalSize - successCount);
        file.setStatus("COMPLETED");
        file.setProcessedAt(LocalDateTime.now());
    }

    private Mono<FinStatementFile> handleAutoSync(FinStatementFile savedFile, Long userId, Boolean autoSync) {
        if (Boolean.TRUE.equals(autoSync)) {
            return transactionFlowService.syncAll(userId)
                    .then(Mono.just(savedFile))
                    .onErrorResume(e -> {
                        log.warn("自动同步失败，但文件已导入", e);
                        return Mono.just(savedFile);
                    });
        }
        return Mono.just(savedFile);
    }

    private Mono<FinStatementFile> markFileAsFailed(FinStatementFile file, String errorMessage) {
        file.setStatus("FAILED");
        file.setErrorLog(errorMessage);
        return statementFileRepository.save(file);
    }

    private void cleanupTempFile(Path tempFile) {
        try {
            Files.deleteIfExists(tempFile);
        } catch (Exception e) {
            log.warn("删除临时文件失败: {}", tempFile, e);
        }
    }
}
