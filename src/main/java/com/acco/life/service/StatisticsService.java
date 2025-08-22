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
    Mono<StatisticsDto> getUserTotalAssets(Integer userId);
    
    /**
     * 获取月度收支统计
     */
    Mono<StatisticsDto> getMonthlyStatistics(Integer userId, LocalDate month);
    
    /**
     * 获取年度收支统计
     */
    Mono<StatisticsDto> getYearlyStatistics(Integer userId, int year);
    
    /**
     * 获取分类统计
     */
    Mono<List<StatisticsDto>> getCategoryStatistics(Integer userId, LocalDate startDate, LocalDate endDate);
    
    /**
     * 获取账户余额统计
     */
    Mono<List<StatisticsDto>> getAccountBalanceStatistics(Integer userId);
    
    /**
     * 获取投资资产统计
     */
    Mono<List<StatisticsDto>> getInvestmentStatistics(Integer userId);
    
    /**
     * 获取固定资产统计
     */
    Mono<List<StatisticsDto>> getFixedAssetStatistics(Integer userId);
    
    /**
     * 获取趋势统计
     */
    Mono<Map<String, Object>> getTrendStatistics(Integer userId, LocalDate startDate, LocalDate endDate);
    
    /**
     * 获取预算执行情况
     */
    Mono<StatisticsDto> getBudgetExecution(Integer userId, LocalDate month);
} 