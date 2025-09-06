package com.acco.life.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 预算数据传输对象
 *
 * @author wensen.zhang
 * @version V1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class BudgetDto extends BaseColumnDto {
    
    /**
     * 用户ID
     */
    private Long userId;
    
    /**
     * 预算名称
     */
    private String name;
    
    /**
     * 预算类型：1-月度预算，2-年度预算，3-分类预算
     */
    private Integer type;
    
    /**
     * 分类ID（分类预算时使用）
     */
    private Long categoryId;
    
    /**
     * 分类名称
     */
    private String categoryName;
    
    /**
     * 预算金额
     */
    private BigDecimal amount;
    
    /**
     * 已使用金额
     */
    private BigDecimal usedAmount;
    
    /**
     * 剩余金额
     */
    private BigDecimal remainingAmount;
    
    /**
     * 使用率
     */
    private BigDecimal usageRate;
    
    /**
     * 预算开始日期
     */
    private LocalDate startDate;
    
    /**
     * 预算结束日期
     */
    private LocalDate endDate;
    
    /**
     * 预算状态：1-进行中，2-已完成，3-已超支
     */
    private Integer status;
    
    /**
     * 状态名称
     */
    private String statusName;
    
    /**
     * 提醒阈值（百分比）
     */
    private BigDecimal alertThreshold;
    
    /**
     * 备注
     */
    private String remark;
} 