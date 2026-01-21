package com.acco.life.controller.fin;

import com.acco.life.dto.fin.FinStatementAccountMapDto;
import com.acco.life.dto.fin.FinTransactionDto;
import com.acco.life.dto.fin.FinTransactionStatementMapDto;
import com.acco.life.service.fin.FinReconciliationService;
import com.acco.life.util.UserUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * 对账归并 Controller
 *
 * @author wensen.zhang
 * @version V2.0.0
 */
@Slf4j
@RestController
@RequestMapping("/api/fin/reconciliation")
@RequiredArgsConstructor
public class FinReconciliationController {

    private final FinReconciliationService reconciliationService;

    /**
     * 自动对账：将账单映射到账户
     */
    @PostMapping("/auto-map-account")
    public Mono<ResponseEntity<List<FinStatementAccountMapDto>>> autoMapStatementToAccount() {
        return UserUtil.getCurrentUserId()
                .flatMap(reconciliationService::autoMapStatementToAccount)
                .map(ResponseEntity::ok);
    }

    /**
     * 手动映射账单到账户
     */
    @PostMapping("/map-account")
    public Mono<ResponseEntity<FinStatementAccountMapDto>> manualMapStatementToAccount(
            @RequestParam Long statementId,
            @RequestParam Long accountId,
            @RequestParam(required = false) String remark) {
        return reconciliationService.manualMapStatementToAccount(statementId, accountId, remark)
                .map(ResponseEntity::ok);
    }

    /**
     * 自动归并：将账单归并到交易
     */
    @PostMapping("/auto-merge-transaction")
    public Mono<ResponseEntity<List<FinTransactionStatementMapDto>>> autoMergeStatementToTransaction() {
        return UserUtil.getCurrentUserId()
                .flatMap(reconciliationService::autoMergeStatementToTransaction)
                .map(ResponseEntity::ok);
    }

    /**
     * 手动归并账单到交易
     */
    @PostMapping("/merge-transaction")
    public Mono<ResponseEntity<FinTransactionStatementMapDto>> manualMergeStatementToTransaction(
            @RequestParam Long statementId,
            @RequestParam Long transactionId,
            @RequestParam(required = false) String mapType) {
        return reconciliationService.manualMergeStatementToTransaction(statementId, transactionId, mapType)
                .map(ResponseEntity::ok);
    }

    /**
     * 从账单创建交易
     */
    @PostMapping("/create-transaction/{statementId}")
    public Mono<ResponseEntity<FinTransactionDto>> createTransactionFromStatement(@PathVariable Long statementId) {
        return reconciliationService.createTransactionFromStatement(statementId)
                .map(ResponseEntity::ok);
    }

    /**
     * 从多条账单创建交易（合单）
     */
    @PostMapping("/create-transaction-batch")
    public Mono<ResponseEntity<FinTransactionDto>> createTransactionFromStatements(
            @RequestBody List<Long> statementIds,
            @RequestParam(required = false) String bizType) {
        return reconciliationService.createTransactionFromStatements(statementIds, bizType)
                .map(ResponseEntity::ok);
    }

    /**
     * 查询账单的账户映射
     */
    @GetMapping("/statement/{statementId}/account-maps")
    public Mono<ResponseEntity<List<FinStatementAccountMapDto>>> findAccountMapsByStatementId(@PathVariable Long statementId) {
        return reconciliationService.findAccountMapsByStatementId(statementId)
                .map(ResponseEntity::ok);
    }

    /**
     * 查询账单的交易映射
     */
    @GetMapping("/statement/{statementId}/transaction-maps")
    public Mono<ResponseEntity<List<FinTransactionStatementMapDto>>> findTransactionMapsByStatementId(@PathVariable Long statementId) {
        return reconciliationService.findTransactionMapsByStatementId(statementId)
                .map(ResponseEntity::ok);
    }

    /**
     * 查询交易的账单映射
     */
    @GetMapping("/transaction/{transactionId}/statement-maps")
    public Mono<ResponseEntity<List<FinTransactionStatementMapDto>>> findStatementMapsByTransactionId(@PathVariable Long transactionId) {
        return reconciliationService.findStatementMapsByTransactionId(transactionId)
                .map(ResponseEntity::ok);
    }

    /**
     * 取消账单-账户映射
     */
    @DeleteMapping("/map-account")
    public Mono<ResponseEntity<Void>> cancelStatementAccountMap(
            @RequestParam Long statementId,
            @RequestParam Long accountId) {
        return reconciliationService.cancelStatementAccountMap(statementId, accountId)
                .then(Mono.just(ResponseEntity.ok().<Void>build()));
    }

    /**
     * 取消账单-交易映射
     */
    @DeleteMapping("/merge-transaction")
    public Mono<ResponseEntity<Void>> cancelStatementTransactionMap(
            @RequestParam Long statementId,
            @RequestParam Long transactionId) {
        return reconciliationService.cancelStatementTransactionMap(statementId, transactionId)
                .then(Mono.just(ResponseEntity.ok().<Void>build()));
    }

    /**
     * 获取对账摘要
     */
    @GetMapping("/summary")
    public Mono<ResponseEntity<FinReconciliationService.ReconciliationSummary>> getReconciliationSummary() {
        return UserUtil.getCurrentUserId()
                .flatMap(reconciliationService::getReconciliationSummary)
                .map(ResponseEntity::ok);
    }
}
