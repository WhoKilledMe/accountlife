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
    Mono<List<BudgetDto>> findByUserId(Integer userId);
    
    /**
     * 根据ID查询预算
     */
    Mono<BudgetDto> findById(Integer id);
    
    /**
     * 保存预算
     */
    Mono<BudgetDto> save(Mono<BudgetDto> budgetDto);
    
    /**
     * 删除预算
     */
    Mono<Void> deleteById(Integer id);
    
    /**
     * 查询用户指定月份的预算
     */
    Mono<List<BudgetDto>> findByUserIdAndMonth(Integer userId, LocalDate month);
    
    /**
     * 查询用户指定年份的预算
     */
    Mono<List<BudgetDto>> findByUserIdAndYear(Integer userId, int year);
    
    /**
     * 查询用户指定分类的预算
     */
    Mono<List<BudgetDto>> findByUserIdAndCategory(Integer userId, Integer categoryId);
    
    /**
     * 更新预算使用金额
     */
    Mono<BudgetDto> updateUsedAmount(Integer budgetId, java.math.BigDecimal usedAmount);
    
    /**
     * 检查预算状态
     */
    Mono<BudgetDto> checkBudgetStatus(Integer budgetId);
    
    /**
     * 获取预算提醒
     */
    Mono<List<BudgetDto>> getBudgetAlerts(Integer userId);
} 