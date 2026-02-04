package com.acco.life.controller.fin;

import com.acco.life.dto.fin.FinTransactionDto;
import com.acco.life.service.fin.FinTransactionFlowService;
import com.acco.life.util.UserUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.List;

/**
 * 完整交易流程 Controller
 * 支持从账单到交易的完整业务流程
 *
 * @author wensen.zhang
 * @version V2.0.0
 */
@Slf4j
@RestController
@RequestMapping("/api/fin/transaction-flow")
@RequiredArgsConstructor
public class FinTransactionFlowController {

    private final FinTransactionFlowService transactionFlowService;

    /**
     * 完整交易流程：从账单创建交易并完成所有后续流程
     * 
     * 流程包括：
     * 1. 创建主交易
     * 2. 自动映射账单到账户
     * 3. 自动映射账单到交易
     * 4. 创建支付路由
     * 5. 创建清算流水
     * 6. 创建会计流水（复式记账）
     * 7. 处理补贴（如果有）
     * 
     * @param request 请求体，包含账单ID列表和可选的交易信息
     * @return 创建的交易信息
     */
    @PostMapping("/complete")
    public Mono<ResponseEntity<FinTransactionDto>> createCompleteTransactionFlow(
            @RequestBody CompleteTransactionFlowRequest request) {
        return transactionFlowService.createCompleteTransactionFlow(
                request.statementIds(), request.transactionDto())
                .map(ResponseEntity::ok);
    }

    /**
     * 完整交易流程请求
     */
    record CompleteTransactionFlowRequest(
            List<Long> statementIds,
            FinTransactionDto transactionDto
    ) {}

    /**
     * 确认交易并完成清算和记账
     * 
     * @param transactionId 交易ID
     * @return 更新后的交易信息
     */
    @PostMapping("/confirm-settle/{transactionId}")
    public Mono<ResponseEntity<FinTransactionDto>> confirmAndSettleTransaction(@PathVariable Long transactionId) {
        return transactionFlowService.confirmAndSettleTransaction(transactionId)
                .map(ResponseEntity::ok);
    }

    /**
     * 创建支付路由（批量）
     * 
     * @param transactionId 交易ID
     * @param routes 支付路由信息列表
     * @return 创建的路由列表
     */
    @PostMapping("/payment-routes/{transactionId}")
    public Mono<ResponseEntity<List<FinTransactionFlowService.PaymentRouteInfo>>> createPaymentRoutes(
            @PathVariable Long transactionId,
            @RequestBody List<FinTransactionFlowService.PaymentRouteInfo> routes) {
        return transactionFlowService.createPaymentRoutes(transactionId, routes)
                .map(ResponseEntity::ok);
    }

    /**
     * 创建清算流水
     * 
     * @param transactionId 交易ID
     * @param fromAccountId 资金来源账户ID
     * @param toAccountId 资金去向账户ID
     * @param amount 清算金额
     * @param remark 备注
     * @return 清算流水ID
     */
    @PostMapping("/clearing-flow/{transactionId}")
    public Mono<ResponseEntity<Long>> createClearingFlow(
            @PathVariable Long transactionId,
            @RequestParam Long fromAccountId,
            @RequestParam Long toAccountId,
            @RequestParam BigDecimal amount,
            @RequestParam(required = false) String remark) {
        return UserUtil.getCurrentUserId()
                .flatMap(userId -> transactionFlowService.createClearingFlow(
                        transactionId, fromAccountId, toAccountId, amount, remark))
                .map(ResponseEntity::ok);
    }

    /**
     * 创建会计流水（复式记账）
     * 
     * @param transactionId 交易ID
     * @param debitAccountId 借方账户ID
     * @param creditAccountId 贷方账户ID
     * @param amount 金额
     * @param bizType 业务类型
     * @param remark 备注
     * @return 创建的流水ID列表（借方+贷方）
     */
    @PostMapping("/account-flow/{transactionId}")
    public Mono<ResponseEntity<List<Long>>> createAccountFlow(
            @PathVariable Long transactionId,
            @RequestParam Long debitAccountId,
            @RequestParam Long creditAccountId,
            @RequestParam BigDecimal amount,
            @RequestParam String bizType,
            @RequestParam(required = false) String remark) {
        return transactionFlowService.createAccountFlow(
                transactionId, debitAccountId, creditAccountId, amount, bizType, remark)
                .map(ResponseEntity::ok);
    }

    /**
     * 处理补贴交易
     * 
     * @param originalTransactionId 原交易ID
     * @param subsidyAmount 补贴金额
     * @param fromAccountId 补贴来源账户
     * @param toAccountId 补贴去向账户
     * @param remark 备注
     * @return 创建的补贴交易信息
     */
    @PostMapping("/subsidy")
    public Mono<ResponseEntity<FinTransactionDto>> createSubsidyTransaction(
            @RequestParam Long originalTransactionId,
            @RequestParam BigDecimal subsidyAmount,
            @RequestParam Long fromAccountId,
            @RequestParam Long toAccountId,
            @RequestParam(required = false) String remark) {
        return transactionFlowService.createSubsidyTransaction(
                originalTransactionId, subsidyAmount, fromAccountId, toAccountId, remark)
                .map(ResponseEntity::ok);
    }

    /**
     * 一键同步：自动完成所有流程
     * 
     * 包含：
     * 1. 交易同步：从账单创建/更新交易
     * 2. 支付链路梳理：自动创建支付路由
     * 3. 清算同步：创建清算流水
     * 4. 交易流程同步：创建会计流水、处理补贴
     * 
     * @return 同步结果摘要
     */
    @PostMapping("/sync-all")
    public Mono<ResponseEntity<FinTransactionFlowService.SyncResult>> syncAll() {
        return transactionFlowService.syncAll(null)
                .map(ResponseEntity::ok);
    }
}
