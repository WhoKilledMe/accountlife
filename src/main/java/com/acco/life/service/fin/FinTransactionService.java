package com.acco.life.service.fin;

import com.acco.life.common.PageResponse;
import com.acco.life.dto.fin.FinPaymentRouteDto;
import com.acco.life.dto.fin.FinTransactionDto;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 交易中枢服务接口
 *
 * @author wensen.zhang
 * @version V2.0.0
 */
public interface FinTransactionService {

    /**
     * 创建交易
     */
    Mono<FinTransactionDto> create(FinTransactionDto dto);

    /**
     * 更新交易
     */
    Mono<FinTransactionDto> update(FinTransactionDto dto);

    /**
     * 根据ID查询（包含支付路由和账单映射）
     */
    Mono<FinTransactionDto> findById(Long id);

    /**
     * 根据交易号查询
     */
    Mono<FinTransactionDto> findByTransactionNo(String transactionNo);

    /**
     * 根据用户ID和时间范围查询
     */
    Mono<List<FinTransactionDto>> findByUserIdAndTimeRange(Long userId, LocalDateTime startTime, LocalDateTime endTime);

    /**
     * 分页查询
     */
    Mono<PageResponse<FinTransactionDto>> page(Long userId, String bizType, String status, String platformCode, Long categoryId, int page, int size);

    /**
     * 删除交易（软删除）
     */
    Mono<Void> deleteById(Long id);

    /**
     * 生成交易号
     */
    Mono<String> generateTransactionNo();

    /**
     * 确认交易
     */
    Mono<FinTransactionDto> confirmTransaction(Long transactionId);

    /**
     * 取消交易
     */
    Mono<FinTransactionDto> cancelTransaction(Long transactionId);

    /**
     * 添加支付路由
     */
    Mono<FinPaymentRouteDto> addPaymentRoute(FinPaymentRouteDto routeDto);

    /**
     * 查询交易的支付路由
     */
    Mono<List<FinPaymentRouteDto>> findPaymentRoutes(Long transactionId);
}
