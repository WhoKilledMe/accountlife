package com.acco.life.service;

import com.acco.life.dto.BudgetDto;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.util.List;

/**
 * 预算服务接口
 *
 * @author wensen.zhang
 * @version V1.0.0
 */
public interface BudgetService {
    
    /**
     * 查询用户所有预算
     */
    Mono<List<BudgetDto>> findByUserId(Long userId);
    
    /**
     * 根据ID查询预算
     */
    Mono<BudgetDto> findById(Long id);
    
    /**
     * 保存预算
     */
    Mono<BudgetDto> save(Mono<BudgetDto> budgetDto);
    
    /**
     * 删除预算
     */
    Mono<Void> deleteById(Long id);
    
    /**
     * 查询用户指定月份的预算
     */
    Mono<List<BudgetDto>> findByUserIdAndMonth(Long userId, LocalDate month);
    
    /**
     * 查询用户指定年份的预算
     */
    Mono<List<BudgetDto>> findByUserIdAndYear(Long userId, int year);
    
    /**
     * 查询用户指定分类的预算
     */
    Mono<List<BudgetDto>> findByUserIdAndCategory(Long userId, Long categoryId);
    
    /**
     * 更新预算使用金额
     */
    Mono<BudgetDto> updateUsedAmount(Long budgetId, java.math.BigDecimal usedAmount);
    
    /**
     * 检查预算状态
     */
    Mono<BudgetDto> checkBudgetStatus(Long budgetId);
    
    /**
     * 获取预算提醒
     */
    Mono<List<BudgetDto>> getBudgetAlerts(Long userId);
    
    /**
     * 获取用户当前月份预算使用情况
     */
    Mono<List<BudgetDto>> getCurrentMonthBudgetUsage();
    
    /**
     * 获取用户指定月份预算使用情况
     */
    Mono<List<BudgetDto>> getMonthBudgetUsage(LocalDate month);
} 