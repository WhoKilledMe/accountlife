package com.acco.life.service.fin;

import com.acco.life.common.PageResponse;
import com.acco.life.dto.fin.FinStatementDto;
import com.acco.life.dto.fin.FinStatementFileDto;
import reactor.core.publisher.Mono;

import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 账单导入与解析服务接口
 *
 * @author wensen.zhang
 * @version V2.0.0
 */
public interface FinStatementService {

    /**
     * 导入账单文件
     * 
     * @param userId 用户ID
     * @param platformCode 平台代码
     * @param fileName 文件名
     * @param fileMd5 文件MD5
     * @param inputStream 文件输入流
     * @param sourceChannel 导入渠道
     * @return 文件导入记录
     */
    Mono<FinStatementFileDto> importFile(Long userId, String platformCode, String fileName, String fileMd5, 
                                          InputStream inputStream, String sourceChannel);

    /**
     * 检查文件是否已导入（幂等检查）
     */
    Mono<Boolean> isFileImported(String fileMd5);

    /**
     * 根据文件ID查询账单文件记录
     */
    Mono<FinStatementFileDto> findFileById(Long fileId);

    /**
     * 分页查询账单文件记录
     */
    Mono<PageResponse<FinStatementFileDto>> pageFiles(Long userId, String platformCode, String status, int page, int size);

    /**
     * 根据ID查询账单行
     */
    Mono<FinStatementDto> findStatementById(Long id);

    /**
     * 根据文件ID查询账单行
     */
    Mono<List<FinStatementDto>> findStatementsByFileId(Long fileId);

    /**
     * 根据用户ID和时间范围查询账单行
     */
    Mono<List<FinStatementDto>> findStatementsByUserIdAndTimeRange(Long userId, LocalDateTime startTime, LocalDateTime endTime);

    /**
     * 分页查询账单行
     */
    Mono<PageResponse<FinStatementDto>> pageStatements(Long userId, Long fileId, String platformCode, String status, int page, int size);

    /**
     * 更新账单行状态
     */
    Mono<FinStatementDto> updateStatementStatus(Long statementId, String status);

    /**
     * 查询待对账的账单
     */
    Mono<List<FinStatementDto>> findPendingReconciliation(Long userId);

    /**
     * 忽略账单行
     */
    Mono<Void> ignoreStatement(Long statementId);

    /**
     * 批量忽略账单行
     */
    Mono<Void> ignoreStatements(List<Long> statementIds);
}
