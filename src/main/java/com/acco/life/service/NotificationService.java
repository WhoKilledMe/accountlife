package com.acco.life.service;

import com.acco.life.dto.NotificationDto;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * 通知服务接口
 *
 * @author wensen.zhang
 * @version V1.0.0
 */
public interface NotificationService {
    
    /**
     * 发送预算提醒通知
     */
    Mono<Void> sendBudgetAlert(Integer userId, String message);
    
    /**
     * 发送交易通知
     */
    Mono<Void> sendTransactionNotification(Integer userId, String message);
    
    /**
     * 发送系统通知
     */
    Mono<Void> sendSystemNotification(Integer userId, String message);
    
    /**
     * 查询用户通知
     */
    Mono<List<NotificationDto>> findByUserId(Integer userId);
    
    /**
     * 标记通知为已读
     */
    Mono<Void> markAsRead(Integer notificationId);
    
    /**
     * 删除通知
     */
    Mono<Void> deleteNotification(Integer notificationId);
    
    /**
     * 获取未读通知数量
     */
    Mono<Long> getUnreadCount(Integer userId);
} 