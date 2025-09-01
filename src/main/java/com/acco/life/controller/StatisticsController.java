package com.acco.life.controller;

import com.acco.life.common.ApiResponse;
import com.acco.life.dto.StatisticsDto;
import com.acco.life.service.StatisticsService;
import com.acco.life.util.UserUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
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
    @GetMapping("/total-assets")
    public Mono<ApiResponse<StatisticsDto>> getTotalAssets() {
        return UserUtil.getCurrentUserId()
                .flatMap(statisticsService::getUserTotalAssets)
                .map(ApiResponse::success);
    }

    @Operation(summary = "获取月度收支统计")
    @GetMapping("/monthly")
    public Mono<ApiResponse<StatisticsDto>> getMonthlyStatistics(
            @Parameter(description = "统计月份，格式yyyy-MM") @RequestParam("month") String month) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate firstDayOfMonth = LocalDate.parse(month + "-01", formatter);
        return UserUtil.getCurrentUserId()
                .flatMap(userId -> statisticsService.getMonthlyStatistics(userId, firstDayOfMonth))
                .map(ApiResponse::success);
    }

    @Operation(summary = "获取年度收支统计")
    @GetMapping("/yearly")
    public Mono<ApiResponse<StatisticsDto>> getYearlyStatistics(
            @Parameter(description = "统计年份") @RequestParam int year) {
        return UserUtil.getCurrentUserId().flatMap(userId -> statisticsService.getYearlyStatistics(userId, year))
                .map(ApiResponse::success);
    }

    @Operation(summary = "获取分类统计")
    @GetMapping("/category")
    public Mono<ApiResponse<List<StatisticsDto>>> getCategoryStatistics(
            @Parameter(description = "开始日期") @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @Parameter(description = "结束日期") @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate) {
        return UserUtil.getCurrentUserId()
                .flatMap(userId -> statisticsService.getCategoryStatistics(userId, startDate, endDate))
                .map(ApiResponse::success);
    }

    @Operation(summary = "获取账户余额统计")
    @GetMapping("/account-balance")
    public Mono<ApiResponse<List<StatisticsDto>>> getAccountBalanceStatistics() {
        return UserUtil.getCurrentUserId()
                .flatMap(statisticsService::getAccountBalanceStatistics)
                .map(ApiResponse::success);
    }

    @Operation(summary = "获取投资资产统计")
    @GetMapping("/investment")
    public Mono<ApiResponse<List<StatisticsDto>>> getInvestmentStatistics() {
        return UserUtil.getCurrentUserId()
                .flatMap(statisticsService::getInvestmentStatistics)
                .map(ApiResponse::success);
    }

    @Operation(summary = "获取固定资产统计")
    @GetMapping("/fixed-asset")
    public Mono<ApiResponse<List<StatisticsDto>>> getFixedAssetStatistics() {
        return UserUtil.getCurrentUserId()
                .flatMap(statisticsService::getFixedAssetStatistics)
                .map(ApiResponse::success);
    }

    @Operation(summary = "获取趋势统计")
    @GetMapping("/trend")
    public Mono<ApiResponse<Map<String, Object>>> getTrendStatistics(
            @Parameter(description = "开始日期") @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @Parameter(description = "结束日期") @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate) {
        return UserUtil.getCurrentUserId()
                .flatMap(userId -> statisticsService.getTrendStatistics(userId, startDate, endDate))
                .map(ApiResponse::success);
    }

    @Operation(summary = "获取预算执行情况")
    @GetMapping("/budget")
    public Mono<ApiResponse<StatisticsDto>> getBudgetExecution(
            @Parameter(description = "统计月份") @RequestParam @DateTimeFormat(pattern = "yyyy-MM") LocalDate month) {
        return UserUtil.getCurrentUserId()
                .flatMap(userId -> statisticsService.getBudgetExecution(userId, month))
                .map(ApiResponse::success);
    }
} 