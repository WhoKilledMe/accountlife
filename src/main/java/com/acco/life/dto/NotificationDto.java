package com.acco.life.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 通知数据传输对象
 *
 * @author wensen.zhang
 * @version V1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationDto {
    
    /**
     * 通知ID
     */
    private Long id;
    
    /**
     * 用户ID
     */
    private Long userId;
    
    /**
     * 通知类型：1-预算提醒，2-交易通知，3-系统通知
     */
    private Integer type;
    
    /**
     * 通知标题
     */
    private String title;
    
    /**
     * 通知内容
     */
    private String content;
    
    /**
     * 通知状态：1-未读，2-已读
     */
    private Integer status;
    
    /**
     * 相关链接
     */
    private String link;
    
    /**
     * 创建时间
     */
    private LocalDateTime createdAt;
    
    /**
     * 阅读时间
     */
    private LocalDateTime readAt;
} 