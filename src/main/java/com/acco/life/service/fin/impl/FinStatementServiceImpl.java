package com.acco.life.service.fin.impl;

import com.acco.life.common.PageResponse;
import com.acco.life.dto.fin.FinStatementDto;
import com.acco.life.dto.fin.FinStatementFileDto;
import com.acco.life.entity.fin.FinStatement;
import com.acco.life.entity.fin.FinStatementFile;
import com.acco.life.enums.fin.StatementStatus;
import com.acco.life.mapper.fin.FinStatementFileMapper;
import com.acco.life.mapper.fin.FinStatementMapper;
import com.acco.life.repository.fin.FinStatementFileRepository;
import com.acco.life.repository.fin.FinStatementRepository;
import com.acco.life.service.fin.FinStatementService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.List;

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
}
