package com.acco.life.controller;

import com.acco.life.common.ApiResponse;
import com.acco.life.dto.BudgetDto;
import com.acco.life.service.BudgetService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * 预算控制器
 *
 * @author wensen.zhang
 * @version V1.0.0
 */
@Tag(name = "Budget 接口")
@RestController
@RequestMapping("/api/budget")
@RequiredArgsConstructor
public class BudgetController {
    
    private final BudgetService budgetService;
    
    @Operation(summary = "查询用户所有预算")
    @GetMapping("/user/{userId}")
    public Mono<ApiResponse<List<BudgetDto>>> findByUserId(
            @Parameter(description = "用户ID") @PathVariable Integer userId) {
        return budgetService.findByUserId(userId)
                .map(ApiResponse::success);
    }
    
    @Operation(summary = "根据ID查询预算")
    @GetMapping("/{id}")
    public Mono<ApiResponse<BudgetDto>> findById(
            @Parameter(description = "预算ID") @PathVariable Integer id) {
        return budgetService.findById(id)
                .map(ApiResponse::success);
    }
    
    @Operation(summary = "创建预算")
    @PostMapping
    public Mono<ApiResponse<BudgetDto>> create(@RequestBody Mono<BudgetDto> budgetDto) {
        return budgetService.save(budgetDto)
                .map(ApiResponse::success);
    }
    
    @Operation(summary = "更新预算")
    @PutMapping
    public Mono<ApiResponse<BudgetDto>> update(@RequestBody Mono<BudgetDto> budgetDto) {
        return budgetService.save(budgetDto)
                .map(ApiResponse::success);
    }
    
    @Operation(summary = "删除预算")
    @DeleteMapping("/{id}")
    public Mono<ApiResponse<Void>> deleteById(
            @Parameter(description = "预算ID") @PathVariable Integer id) {
        return budgetService.deleteById(id)
                .then(Mono.just(ApiResponse.success()));
    }
    
    @Operation(summary = "查询用户指定月份的预算")
    @GetMapping("/user/{userId}/month")
    public Mono<ApiResponse<List<BudgetDto>>> findByUserIdAndMonth(
            @Parameter(description = "用户ID") @PathVariable Integer userId,
            @Parameter(description = "统计月份") @RequestParam @DateTimeFormat(pattern = "yyyy-MM") LocalDate month) {
        return budgetService.findByUserIdAndMonth(userId, month)
                .map(ApiResponse::success);
    }
    
    @Operation(summary = "查询用户指定年份的预算")
    @GetMapping("/user/{userId}/year")
    public Mono<ApiResponse<List<BudgetDto>>> findByUserIdAndYear(
            @Parameter(description = "用户ID") @PathVariable Integer userId,
            @Parameter(description = "统计年份") @RequestParam int year) {
        return budgetService.findByUserIdAndYear(userId, year)
                .map(ApiResponse::success);
    }
    
    @Operation(summary = "查询用户指定分类的预算")
    @GetMapping("/user/{userId}/category/{categoryId}")
    public Mono<ApiResponse<List<BudgetDto>>> findByUserIdAndCategory(
            @Parameter(description = "用户ID") @PathVariable Integer userId,
            @Parameter(description = "分类ID") @PathVariable Integer categoryId) {
        return budgetService.findByUserIdAndCategory(userId, categoryId)
                .map(ApiResponse::success);
    }
    
    @Operation(summary = "更新预算使用金额")
    @PutMapping("/{id}/used-amount")
    public Mono<ApiResponse<BudgetDto>> updateUsedAmount(
            @Parameter(description = "预算ID") @PathVariable Integer id,
            @Parameter(description = "已使用金额") @RequestParam BigDecimal usedAmount) {
        return budgetService.updateUsedAmount(id, usedAmount)
                .map(ApiResponse::success);
    }
    
    @Operation(summary = "检查预算状态")
    @PutMapping("/{id}/check-status")
    public Mono<ApiResponse<BudgetDto>> checkBudgetStatus(
            @Parameter(description = "预算ID") @PathVariable Integer id) {
        return budgetService.checkBudgetStatus(id)
                .map(ApiResponse::success);
    }
    
    @Operation(summary = "获取预算提醒")
    @GetMapping("/user/{userId}/alerts")
    public Mono<ApiResponse<List<BudgetDto>>> getBudgetAlerts(
            @Parameter(description = "用户ID") @PathVariable Integer userId) {
        return budgetService.getBudgetAlerts(userId)
                .map(ApiResponse::success);
    }
} 