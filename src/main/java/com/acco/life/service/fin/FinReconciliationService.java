package com.acco.life.service.fin;

import com.acco.life.dto.fin.FinStatementAccountMapDto;
import com.acco.life.dto.fin.FinStatementDto;
import com.acco.life.dto.fin.FinTransactionDto;
import com.acco.life.dto.fin.FinTransactionStatementMapDto;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * 对账归并服务接口
 *
 * @author wensen.zhang
 * @version V2.0.0
 */
public interface FinReconciliationService {

    /**
     * 自动对账：将账单映射到账户
     * 
     * @param userId 用户ID
     * @return 映射结果列表
     */
    Mono<List<FinStatementAccountMapDto>> autoMapStatementToAccount(Long userId);

    /**
     * 手动映射账单到账户
     */
    Mono<FinStatementAccountMapDto> manualMapStatementToAccount(Long statementId, Long accountId, String remark);

    /**
     * 自动归并：将账单归并到交易
     * 
     * @param userId 用户ID
     * @return 归并结果
     */
    Mono<List<FinTransactionStatementMapDto>> autoMergeStatementToTransaction(Long userId);

    /**
     * 手动归并账单到交易
     */
    Mono<FinTransactionStatementMapDto> manualMergeStatementToTransaction(Long statementId, Long transactionId, String mapType);

    /**
     * 从账单创建交易
     */
    Mono<FinTransactionDto> createTransactionFromStatement(Long statementId);

    /**
     * 从多条账单创建交易（合单）
     */
    Mono<FinTransactionDto> createTransactionFromStatements(List<Long> statementIds, String bizType);

    /**
     * 查询账单的账户映射
     */
    Mono<List<FinStatementAccountMapDto>> findAccountMapsByStatementId(Long statementId);

    /**
     * 查询账单的交易映射
     */
    Mono<List<FinTransactionStatementMapDto>> findTransactionMapsByStatementId(Long statementId);

    /**
     * 查询交易的账单映射
     */
    Mono<List<FinTransactionStatementMapDto>> findStatementMapsByTransactionId(Long transactionId);

    /**
     * 取消账单-账户映射
     */
    Mono<Void> cancelStatementAccountMap(Long statementId, Long accountId);

    /**
     * 取消账单-交易映射
     */
    Mono<Void> cancelStatementTransactionMap(Long statementId, Long transactionId);

    /**
     * 获取待对账账单摘要
     */
    Mono<ReconciliationSummary> getReconciliationSummary(Long userId);

    /**
     * 对账摘要
     */
    record ReconciliationSummary(
            int totalPending,
            int mappedToAccount,
            int mappedToTransaction,
            int ignored
    ) {}
}
