package com.acco.life.controller;

import com.acco.life.common.ApiResponse;
import com.acco.life.common.PageResponse;
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
            @Parameter(description = "用户ID") @PathVariable Long userId) {
        return budgetService.findByUserId(userId)
                .map(ApiResponse::success);
    }

    @Operation(summary = "分页查询用户预算")
    @GetMapping("/user/{userId}/page")
    public Mono<ApiResponse<PageResponse<BudgetDto>>> pageByUserId(
            @Parameter(description = "用户ID") @PathVariable Long userId,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "10") int size) {
        return budgetService.findByUserId(userId)
                .map(list -> ApiResponse.success(PageResponse.fromList(list, page, size)));
    }
    
    @Operation(summary = "根据ID查询预算")
    @GetMapping("/{id}")
    public Mono<ApiResponse<BudgetDto>> findById(
            @Parameter(description = "预算ID") @PathVariable Long id) {
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
            @Parameter(description = "预算ID") @PathVariable Long id) {
        return budgetService.deleteById(id)
                .then(Mono.just(ApiResponse.success()));
    }
    
    @Operation(summary = "查询用户指定月份的预算")
    @GetMapping("/user/{userId}/month")
    public Mono<ApiResponse<List<BudgetDto>>> findByUserIdAndMonth(
            @Parameter(description = "用户ID") @PathVariable Long userId,
            @Parameter(description = "统计月份") @RequestParam @DateTimeFormat(pattern = "yyyy-MM") LocalDate month) {
        return budgetService.findByUserIdAndMonth(userId, month)
                .map(ApiResponse::success);
    }
    
    @Operation(summary = "查询用户指定年份的预算")
    @GetMapping("/user/{userId}/year")
    public Mono<ApiResponse<List<BudgetDto>>> findByUserIdAndYear(
            @Parameter(description = "用户ID") @PathVariable Long userId,
            @Parameter(description = "统计年份") @RequestParam int year) {
        return budgetService.findByUserIdAndYear(userId, year)
                .map(ApiResponse::success);
    }
    
    @Operation(summary = "查询用户指定分类的预算")
    @GetMapping("/user/{userId}/category/{categoryId}")
    public Mono<ApiResponse<List<BudgetDto>>> findByUserIdAndCategory(
            @Parameter(description = "用户ID") @PathVariable Long userId,
            @Parameter(description = "分类ID") @PathVariable Long categoryId) {
        return budgetService.findByUserIdAndCategory(userId, categoryId)
                .map(ApiResponse::success);
    }
    
    @Operation(summary = "更新预算使用金额")
    @PutMapping("/{id}/used-amount")
    public Mono<ApiResponse<BudgetDto>> updateUsedAmount(
            @Parameter(description = "预算ID") @PathVariable Long id,
            @Parameter(description = "已使用金额") @RequestParam BigDecimal usedAmount) {
        return budgetService.updateUsedAmount(id, usedAmount)
                .map(ApiResponse::success);
    }
    
    @Operation(summary = "检查预算状态")
    @PutMapping("/{id}/check-status")
    public Mono<ApiResponse<BudgetDto>> checkBudgetStatus(
            @Parameter(description = "预算ID") @PathVariable Long id) {
        return budgetService.checkBudgetStatus(id)
                .map(ApiResponse::success);
    }
    
    @Operation(summary = "获取预算提醒")
    @GetMapping("/user/{userId}/alerts")
    public Mono<ApiResponse<List<BudgetDto>>> getBudgetAlerts(
            @Parameter(description = "用户ID") @PathVariable Long userId) {
        return budgetService.getBudgetAlerts(userId)
                .map(ApiResponse::success);
    }
    
    @Operation(summary = "获取用户当前月份预算使用情况")
    @GetMapping("/usage/current-month")
    public Mono<ApiResponse<List<BudgetDto>>> getCurrentMonthBudgetUsage() {
        return budgetService.getCurrentMonthBudgetUsage()
                .map(ApiResponse::success);
    }
    
    @Operation(summary = "获取用户指定月份预算使用情况")
    @GetMapping("/usage/month")
    public Mono<ApiResponse<List<BudgetDto>>> getMonthBudgetUsage(
            @Parameter(description = "统计月份，格式yyyy-MM") @RequestParam @DateTimeFormat(pattern = "yyyy-MM") LocalDate month) {
        return budgetService.getMonthBudgetUsage(month)
                .map(ApiResponse::success);
    }
} 