package com.acco.life.controller;

import com.acco.life.common.ApiResponse;
import com.acco.life.dto.StatisticsDto;
import com.acco.life.service.StatisticsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * 统计控制器
 *
 * @author wensen.zhang
 * @version V1.0.0
 */
@Tag(name = "Statistics 接口")
@RestController
@RequestMapping("/api/statistics")
@RequiredArgsConstructor
public class StatisticsController {
    
    private final StatisticsService statisticsService;
    
    @Operation(summary = "获取用户总资产统计")
    @GetMapping("/total-assets/{userId}")
    public Mono<ApiResponse<StatisticsDto>> getTotalAssets(
            @Parameter(description = "用户ID") @PathVariable Integer userId) {
        return statisticsService.getUserTotalAssets(userId)
                .map(ApiResponse::success);
    }
    
    @Operation(summary = "获取月度收支统计")
    @GetMapping("/monthly/{userId}")
    public Mono<ApiResponse<StatisticsDto>> getMonthlyStatistics(
            @Parameter(description = "用户ID") @PathVariable Integer userId,
            @Parameter(description = "统计月份") @RequestParam @DateTimeFormat(pattern = "yyyy-MM") LocalDate month) {
        return statisticsService.getMonthlyStatistics(userId, month)
                .map(ApiResponse::success);
    }
    
    @Operation(summary = "获取年度收支统计")
    @GetMapping("/yearly/{userId}")
    public Mono<ApiResponse<StatisticsDto>> getYearlyStatistics(
            @Parameter(description = "用户ID") @PathVariable Integer userId,
            @Parameter(description = "统计年份") @RequestParam int year) {
        return statisticsService.getYearlyStatistics(userId, year)
                .map(ApiResponse::success);
    }
    
    @Operation(summary = "获取分类统计")
    @GetMapping("/category/{userId}")
    public Mono<ApiResponse<List<StatisticsDto>>> getCategoryStatistics(
            @Parameter(description = "用户ID") @PathVariable Integer userId,
            @Parameter(description = "开始日期") @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @Parameter(description = "结束日期") @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate) {
        return statisticsService.getCategoryStatistics(userId, startDate, endDate)
                .map(ApiResponse::success);
    }
    
    @Operation(summary = "获取账户余额统计")
    @GetMapping("/account-balance/{userId}")
    public Mono<ApiResponse<List<StatisticsDto>>> getAccountBalanceStatistics(
            @Parameter(description = "用户ID") @PathVariable Integer userId) {
        return statisticsService.getAccountBalanceStatistics(userId)
                .map(ApiResponse::success);
    }
    
    @Operation(summary = "获取投资资产统计")
    @GetMapping("/investment/{userId}")
    public Mono<ApiResponse<List<StatisticsDto>>> getInvestmentStatistics(
            @Parameter(description = "用户ID") @PathVariable Integer userId) {
        return statisticsService.getInvestmentStatistics(userId)
                .map(ApiResponse::success);
    }
    
    @Operation(summary = "获取固定资产统计")
    @GetMapping("/fixed-asset/{userId}")
    public Mono<ApiResponse<List<StatisticsDto>>> getFixedAssetStatistics(
            @Parameter(description = "用户ID") @PathVariable Integer userId) {
        return statisticsService.getFixedAssetStatistics(userId)
                .map(ApiResponse::success);
    }
    
    @Operation(summary = "获取趋势统计")
    @GetMapping("/trend/{userId}")
    public Mono<ApiResponse<Map<String, Object>>> getTrendStatistics(
            @Parameter(description = "用户ID") @PathVariable Integer userId,
            @Parameter(description = "开始日期") @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @Parameter(description = "结束日期") @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate) {
        return statisticsService.getTrendStatistics(userId, startDate, endDate)
                .map(ApiResponse::success);
    }
    
    @Operation(summary = "获取预算执行情况")
    @GetMapping("/budget/{userId}")
    public Mono<ApiResponse<StatisticsDto>> getBudgetExecution(
            @Parameter(description = "用户ID") @PathVariable Integer userId,
            @Parameter(description = "统计月份") @RequestParam @DateTimeFormat(pattern = "yyyy-MM") LocalDate month) {
        return statisticsService.getBudgetExecution(userId, month)
                .map(ApiResponse::success);
    }
} 