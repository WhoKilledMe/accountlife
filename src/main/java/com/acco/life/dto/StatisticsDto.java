package com.acco.life.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 统计数据传输对象
 *
 * @author wensen.zhang
 * @version V1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StatisticsDto {
    
    /**
     * 统计ID
     */
    private Integer id;
    
    /**
     * 用户ID
     */
    private Integer userId;
    
    /**
     * 统计类型
     */
    private String type;
    
    /**
     * 统计名称
     */
    private String name;
    
    /**
     * 统计值
     */
    private BigDecimal value;
    
    /**
     * 统计单位
     */
    private String unit;
    
    /**
     * 统计日期
     */
    private LocalDate date;
    
    /**
     * 统计周期（日/月/年）
     */
    private String period;
    
    /**
     * 分类ID
     */
    private Integer categoryId;
    
    /**
     * 分类名称
     */
    private String categoryName;
    
    /**
     * 账户ID
     */
    private Integer accountId;
    
    /**
     * 账户名称
     */
    private String accountName;
    
    /**
     * 收入金额
     */
    private BigDecimal income;
    
    /**
     * 支出金额
     */
    private BigDecimal expense;
    
    /**
     * 余额
     */
    private BigDecimal balance;
    
    /**
     * 百分比
     */
    private BigDecimal percentage;
    
    /**
     * 目标值
     */
    private BigDecimal target;
    
    /**
     * 完成率
     */
    private BigDecimal completionRate;
    
    /**
     * 创建时间
     */
    private LocalDateTime createdAt;
    
    /**
     * 更新时间
     */
    private LocalDateTime updatedAt;
} 