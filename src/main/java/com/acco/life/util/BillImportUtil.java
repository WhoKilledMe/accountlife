package com.acco.life.util;

import com.acco.life.dto.FileTransactionDto;
import com.acco.life.entity.fin.FinStatement;
import com.acco.life.entity.fin.FinStatementFile;
import com.acco.life.enums.TransactionSourceType;
import com.acco.life.repository.fin.FinStatementFileRepository;
import com.acco.life.repository.fin.FinStatementRepository;
import com.acco.life.service.AiTransactionCategoryService;
import com.acco.life.service.csv.FinStatementCsvParser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.DigestUtils;
import reactor.core.publisher.Mono;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 账单导入工具类
 * 用于批量导入账单文件生成交易记录
 *
 * @author wensen.zhang
 */
@Slf4j
@Component
public class BillImportUtil {

    private final List<FinStatementCsvParser> csvParsers;
    private final FinStatementFileRepository statementFileRepository;
    private final FinStatementRepository statementRepository;
    private final AiTransactionCategoryService categoryService;

    public BillImportUtil(List<FinStatementCsvParser> csvParsers,
                         FinStatementFileRepository statementFileRepository,
                         FinStatementRepository statementRepository,
                         AiTransactionCategoryService categoryService) {
        this.csvParsers = csvParsers;
        this.statementFileRepository = statementFileRepository;
        this.statementRepository = statementRepository;
        this.categoryService = categoryService;
    }

    /**
     * 导入账单文件并生成交易记录
     *
     * @param filePath 文件路径
     * @param type 交易来源类型
     * @param userId 用户ID
     * @return 导入结果
     */
    public Mono<ImportResult> importBillFile(String filePath, TransactionSourceType type, Long userId) {
        return Mono.fromCallable(() -> {
            File file = new File(filePath);
            if (!file.exists()) {
                throw new IllegalArgumentException("文件不存在: " + filePath);
            }

            // 计算文件MD5
            byte[] fileBytes = Files.readAllBytes(Paths.get(filePath));
            String fileMd5 = DigestUtils.md5DigestAsHex(fileBytes != null ? fileBytes : new byte[0]);

            // 检查文件是否已导入
            Boolean imported = statementFileRepository.findByFileMd5(fileMd5)
                    .hasElement()
                    .block();
            if (Boolean.TRUE.equals(imported)) {
                log.warn("文件已导入，跳过: {}", filePath);
                return new ImportResult(false, "文件已导入", fileMd5, 0, 0, 0);
            }

            // 获取对应的解析器
            FinStatementCsvParser parser = csvParsers.stream()
                    .filter(p -> p.supports(type))
                    .findFirst()
                    .orElseThrow(() -> new IllegalArgumentException("未找到匹配的解析器: " + type));

            // 创建文件记录
            FinStatementFile fileRecord = new FinStatementFile();
            fileRecord.setUserId(userId);
            fileRecord.setPlatformCode(parser.getPlatformCode());
            fileRecord.setFileName(file.getName());
            fileRecord.setFileMd5(fileMd5);
            fileRecord.setFileType(getFileType(file.getName()));
            fileRecord.setSourceChannel("BATCH_IMPORT");
            fileRecord.setStatus("PROCESSING");
            fileRecord.setUploadedAt(LocalDateTime.now());

            FinStatementFile savedFile = statementFileRepository.save(fileRecord).block();

                try {
                    // 解析文件
                    try (InputStream inputStream = new FileInputStream(file)) {
                        List<? extends FileTransactionDto> dtos = parser.parse(inputStream);
                        List<FinStatement> statements = parser.buildStatements(dtos, userId, savedFile.getId(), categoryService);

                    // 保存账单行
                    List<FinStatement> savedStatements = statementRepository.saveAll(statements)
                            .collectList()
                            .blockOptional()
                            .orElse(List.of());

                    // 更新文件记录
                    savedFile.setTotalRows(dtos.size());
                    savedFile.setSuccessCount(savedStatements != null ? savedStatements.size() : 0);
                    savedFile.setFailureCount(dtos.size() - (savedStatements != null ? savedStatements.size() : 0));
                    savedFile.setStatus("COMPLETED");
                    savedFile.setProcessedAt(LocalDateTime.now());
                    statementFileRepository.save(savedFile).block();

                    log.info("文件导入成功: {}, 总行数: {}, 成功: {}, 失败: {}",
                            filePath, dtos.size(), savedStatements != null ? savedStatements.size() : 0,
                            dtos.size() - (savedStatements != null ? savedStatements.size() : 0));

                    return new ImportResult(true, "导入成功", fileMd5,
                            dtos.size(), savedStatements != null ? savedStatements.size() : 0,
                            dtos.size() - (savedStatements != null ? savedStatements.size() : 0));
                }
            } catch (Exception e) {
                log.error("解析文件失败: {}", filePath, e);
                savedFile.setStatus("FAILED");
                savedFile.setErrorLog(e.getMessage());
                statementFileRepository.save(savedFile).block();
                return new ImportResult(false, "解析失败: " + e.getMessage(), fileMd5, 0, 0, 0);
            }
        });
    }

    /**
     * 批量导入账单文件
     *
     * @param filePaths 文件路径列表
     * @param type 交易来源类型
     * @param userId 用户ID
     * @return 导入结果列表
     */
    public Mono<List<ImportResult>> importBillFiles(List<String> filePaths, TransactionSourceType type, Long userId) {
        return Mono.fromCallable(() -> {
            return filePaths.stream()
                    .map(filePath -> importBillFile(filePath, type, userId).block())
                    .toList();
        });
    }

    private String getFileType(String fileName) {
        if (fileName == null) {
            return "UNKNOWN";
        }
        String lowerName = fileName.toLowerCase();
        if (lowerName.endsWith(".csv")) {
            return "CSV";
        } else if (lowerName.endsWith(".xlsx")) {
            return "XLSX";
        } else if (lowerName.endsWith(".zip")) {
            return "ZIP";
        } else if (lowerName.endsWith(".json")) {
            return "JSON";
        } else if (lowerName.endsWith(".pdf")) {
            return "PDF";
        }
        return "UNKNOWN";
    }

    /**
     * 导入结果
     */
    public static class ImportResult {
        private final boolean success;
        private final String message;
        private final String fileMd5;
        private final int totalRows;
        private final int successCount;
        private final int failureCount;

        public ImportResult(boolean success, String message, String fileMd5,
                           int totalRows, int successCount, int failureCount) {
            this.success = success;
            this.message = message;
            this.fileMd5 = fileMd5;
            this.totalRows = totalRows;
            this.successCount = successCount;
            this.failureCount = failureCount;
        }

        public boolean isSuccess() {
            return success;
        }

        public String getMessage() {
            return message;
        }

        public String getFileMd5() {
            return fileMd5;
        }

        public int getTotalRows() {
            return totalRows;
        }

        public int getSuccessCount() {
            return successCount;
        }

        public int getFailureCount() {
            return failureCount;
        }

        @Override
        public String toString() {
            return String.format("ImportResult{success=%s, message='%s', fileMd5='%s', totalRows=%d, successCount=%d, failureCount=%d}",
                    success, message, fileMd5, totalRows, successCount, failureCount);
        }
    }
}
