package com.acco.life.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 预算实体类
 *
 * @author wensen.zhang
 * @version V1.0.0
 */
@EqualsAndHashCode(callSuper = true)
@Table("budget")
@Data
public class Budget extends BaseColumnEntity {
    
    /**
     * 用户ID
     */
    @Column("user_id")
    private Long userId;
    
    /**
     * 预算名称
     */
    @Column("name")
    private String name;
    
    /**
     * 预算类型：1-月度预算，2-年度预算，3-分类预算
     */
    @Column("period_type")
    private Integer type;
    
    /**
     * 分类ID（分类预算时使用）
     */
    @Column("category_id")
    private Long categoryId;
    
    /**
     * 预算金额
     */
    @Column("amount")
    private BigDecimal amount;
    
    /**
     * 已使用金额
     */
    @Column("used_amount")
    private BigDecimal usedAmount;
    
    /**
     * 预算开始日期
     */
    @Column("start_date")
    private LocalDate startDate;
    
    /**
     * 预算结束日期
     */
    @Column("end_date")
    private LocalDate endDate;
    
    /**
     * 预算状态：1-进行中，2-已完成，3-已超支
     */
    @Column("status")
    private Integer status;
    
    /**
     * 提醒阈值（百分比）
     */
    @Column("alert_threshold")
    private BigDecimal alertThreshold;
    
    /**
     * 备注
     */
    @Column("remark")
    private String remark;
} 