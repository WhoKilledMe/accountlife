package com.acco.life.service;

import com.acco.life.dto.StatisticsDto;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * 统计服务接口
 *
 * @author wensen.zhang
 * @version V1.0.0
 */
public interface StatisticsService {
    
    /**
     * 获取用户总资产统计
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
     * 获取账户余额统计
     */
    Mono<List<StatisticsDto>> getAccountBalanceStatistics(Long userId);
    
    /**
     * 获取投资资产统计
     */
    Mono<List<StatisticsDto>> getInvestmentStatistics(Long userId);
    
    /**
     * 获取固定资产统计
     */
    Mono<List<StatisticsDto>> getFixedAssetStatistics(Long userId);
    
    /**
     * 获取趋势统计
     */
    Mono<Map<String, Object>> getTrendStatistics(Long userId, LocalDate startDate, LocalDate endDate);
    
    /**
     * 获取预算执行情况
     */
    Mono<StatisticsDto> getBudgetExecution(Long userId, LocalDate month);
} 