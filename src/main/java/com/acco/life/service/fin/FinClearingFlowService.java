package com.acco.life.service.fin;

import com.acco.life.common.PageResponse;
import com.acco.life.dto.fin.FinClearingFlowDto;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 清算流水服务接口
 * 用于记录银行/渠道等清算中/已清算/失败的资金移动
 *
 * @author wensen.zhang
 * @version V2.0.0
 */
public interface FinClearingFlowService {

    /**
     * 创建清算流水
     * 
     * @param userId 用户ID
     * @param fromAccountId 资金来源账户ID（可NULL表示平台或外部）
     * @param toAccountId 资金去向账户ID（可NULL）
     * @param transactionId 关联的业务交易ID
     * @param amount 清算金额
     * @param currency 币种
     * @param tradeTime 清算时间
     * @param remark 备注
     * @return 创建的清算流水
     */
    Mono<FinClearingFlowDto> createClearingFlow(Long userId, Long fromAccountId, Long toAccountId,
                                                 Long transactionId, BigDecimal amount, String currency,
                                                 LocalDateTime tradeTime, String remark);

    /**
     * 更新清算状态
     * 
     * @param flowId 清算流水ID
     * @param status 新状态：INIT/CLEARING/SUCCESS/FAILED
     * @return 更新后的清算流水
     */
    Mono<FinClearingFlowDto> updateClearingStatus(Long flowId, String status);

    /**
     * 根据ID查询
     */
    Mono<FinClearingFlowDto> findById(Long id);

    /**
     * 根据交易ID查询清算流水
     */
    Mono<List<FinClearingFlowDto>> findByTransactionId(Long transactionId);

    /**
     * 根据账户ID查询清算流水
     */
    Mono<List<FinClearingFlowDto>> findByAccountId(Long accountId);

    /**
     * 根据用户ID和时间范围查询
     */
    Mono<List<FinClearingFlowDto>> findByUserIdAndTimeRange(Long userId, LocalDateTime startTime, LocalDateTime endTime);

    /**
     * 分页查询
     */
    Mono<PageResponse<FinClearingFlowDto>> page(Long userId, String status, Long accountId, int page, int size);
}
