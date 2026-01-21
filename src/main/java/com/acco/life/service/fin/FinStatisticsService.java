package com.acco.life.service.fin;

import com.acco.life.dto.StatisticsDto;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * 新版统计服务接口（基于 fin_* 表结构）
 *
 * @author wensen.zhang
 * @version V2.0.0
 */
public interface FinStatisticsService {

    /**
     * 获取用户总资产
     */
    Mono<StatisticsDto> getUserTotalAssets(Long userId);

    /**
     * 获取月度收支统计
     */
    Mono<StatisticsDto> getMonthlyStatistics(Long userId, LocalDate month);

    /**
     * 获取年度收支统计
     */
    Mono<StatisticsDto> getYearlyStatistics(Long userId, int year);

    /**
     * 获取分类统计
     */
    Mono<List<StatisticsDto>> getCategoryStatistics(Long userId, LocalDate startDate, LocalDate endDate);

    /**
     * 获取各账户余额统计
     */
    Mono<List<StatisticsDto>> getAccountBalanceStatistics(Long userId);

    /**
     * 获取收支趋势统计
     */
    Mono<Map<String, Object>> getTrendStatistics(Long userId, LocalDate startDate, LocalDate endDate);

    /**
     * 获取支付方式统计
     */
    Mono<List<StatisticsDto>> getPaymentMethodStatistics(Long userId, LocalDate startDate, LocalDate endDate);

    /**
     * 获取平台消费统计
     */
    Mono<List<StatisticsDto>> getPlatformStatistics(Long userId, LocalDate startDate, LocalDate endDate);

    /**
     * 获取账单导入统计
     */
    Mono<StatisticsDto> getStatementImportStatistics(Long userId);
}
