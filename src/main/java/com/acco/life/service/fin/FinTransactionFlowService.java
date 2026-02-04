package com.acco.life.service.fin;

import com.acco.life.dto.fin.FinTransactionDto;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.List;

/**
 * 完整交易流程服务接口
 * 支持从账单到交易的完整业务流程，包括：
 * 1. 账单导入和解析
 * 2. 账单到账户映射
 * 3. 账单到交易映射
 * 4. 支付路由创建
 * 5. 清算流水记录
 * 6. 会计流水记录（复式记账）
 * 7. 补贴交易处理
 *
 * @author wensen.zhang
 * @version V2.0.0
 */
public interface FinTransactionFlowService {

    /**
     * 完整交易流程：从账单创建交易并完成所有后续流程
     * 
     * 流程：
     * 1. 创建主交易
     * 2. 自动映射账单到账户
     * 3. 自动映射账单到交易
     * 4. 创建支付路由（根据账单信息推断）
     * 5. 创建清算流水（如果涉及银行清算）
     * 6. 创建会计流水（复式记账）
     * 7. 处理补贴（如果账单中有优惠信息）
     * 
     * @param statementIds 账单ID列表（同一笔交易的多条账单）
     * @param transactionDto 交易信息（可选，如果为null则从账单推断）
     * @return 创建的交易信息（包含所有关联数据）
     */
    Mono<FinTransactionDto> createCompleteTransactionFlow(List<Long> statementIds, FinTransactionDto transactionDto);

    /**
     * 确认交易并完成清算和记账
     * 
     * 流程：
     * 1. 确认交易状态
     * 2. 创建清算流水（如果还未创建）
     * 3. 创建会计流水（复式记账）
     * 4. 更新账户余额（可选）
     * 
     * @param transactionId 交易ID
     * @return 更新后的交易信息
     */
    Mono<FinTransactionDto> confirmAndSettleTransaction(Long transactionId);

    /**
     * 创建支付路由（批量）
     * 
     * @param transactionId 交易ID
     * @param routes 支付路由信息列表
     * @return 创建的路由列表
     */
    Mono<List<PaymentRouteInfo>> createPaymentRoutes(Long transactionId, List<PaymentRouteInfo> routes);

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
    Mono<Long> createClearingFlow(Long transactionId, Long fromAccountId, Long toAccountId, 
                                   BigDecimal amount, String remark);

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
    Mono<List<Long>> createAccountFlow(Long transactionId, Long debitAccountId, Long creditAccountId,
                                        BigDecimal amount, String bizType, String remark);

    /**
     * 处理补贴交易
     * 
     * @param originalTransactionId 原交易ID
     * @param subsidyAmount 补贴金额
     * @param fromAccountId 补贴来源账户（通常是平台补贴账户）
     * @param toAccountId 补贴去向账户（通常是商户账户）
     * @param remark 备注
     * @return 创建的补贴交易信息
     */
    Mono<FinTransactionDto> createSubsidyTransaction(Long originalTransactionId, BigDecimal subsidyAmount,
                                                       Long fromAccountId, Long toAccountId, String remark);

    /**
     * 一键同步：自动完成所有流程
     * 
     * 包含：
     * 1. 交易同步：从账单创建/更新交易
     * 2. 支付链路梳理：自动创建支付路由
     * 3. 清算同步：创建清算流水
     * 4. 交易流程同步：创建会计流水、处理补贴
     * 
     * @param userId 用户ID（可选，如果为null则从当前上下文获取）
     * @return 同步结果摘要
     */
    Mono<SyncResult> syncAll(Long userId);

    /**
     * 同步结果
     */
    record SyncResult(
            int processedTransactions,      // 处理的交易数
            int createdPaymentRoutes,       // 创建的支付路由数
            int createdClearingFlows,       // 创建的清算流水数
            int createdAccountFlows,        // 创建的会计流水数
            int processedSubsidies          // 处理的补贴数
    ) {}

    /**
     * 支付路由信息
     */
    record PaymentRouteInfo(
            Long paymentMethodId,  // 支付方式ID
            Long fromAccountId,     // 资金来源账户ID
            Long toAccountId,       // 资金去向账户ID
            BigDecimal amount,      // 金额
            Integer routeOrder,     // 路由顺序
            String routeType        // 路由类型：NORMAL/SUBSIDY/FEE/SPLIT
    ) {}
}
