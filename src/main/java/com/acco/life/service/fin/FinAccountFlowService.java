package com.acco.life.service.fin;

import com.acco.life.common.PageResponse;
import com.acco.life.dto.fin.FinAccountFlowDto;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 会计流水服务接口
 *
 * @author wensen.zhang
 * @version V2.0.0
 */
public interface FinAccountFlowService {

    /**
     * 创建会计流水（单边）
     */
    Mono<FinAccountFlowDto> createFlow(FinAccountFlowDto dto);

    /**
     * 创建复式记账分录（借贷双边）
     * 
     * @param debitAccountId 借方账户ID
     * @param creditAccountId 贷方账户ID
     * @param amount 金额
     * @param transactionId 关联交易ID
     * @param bizType 业务类型
     * @param remark 备注
     * @return 创建的流水列表（借方+贷方）
     */
    Mono<List<FinAccountFlowDto>> createDoubleEntry(Long userId, Long debitAccountId, Long creditAccountId, 
                                                     BigDecimal amount, Long transactionId, String bizType, String remark);

    /**
     * 根据ID查询
     */
    Mono<FinAccountFlowDto> findById(Long id);

    /**
     * 根据账户ID查询流水
     */
    Mono<List<FinAccountFlowDto>> findByAccountId(Long accountId);

    /**
     * 根据交易ID查询流水
     */
    Mono<List<FinAccountFlowDto>> findByTransactionId(Long transactionId);

    /**
     * 根据账户ID和时间范围查询
     */
    Mono<List<FinAccountFlowDto>> findByAccountIdAndTimeRange(Long accountId, LocalDateTime startTime, LocalDateTime endTime);

    /**
     * 分页查询
     */
    Mono<PageResponse<FinAccountFlowDto>> page(Long userId, Long accountId, String direction, String bizType, int page, int size);

    /**
     * 验证会计分录组的借贷平衡
     */
    Mono<Boolean> validateJournalBalance(Long journalId);

    /**
     * 计算账户在指定时间点的余额
     */
    Mono<BigDecimal> calculateBalanceAt(Long accountId, LocalDateTime dateTime);

    /**
     * 生成会计流水号
     */
    Mono<String> generateFlowNo();

    /**
     * 生成会计分录组ID
     */
    Mono<Long> generateJournalId();
}
