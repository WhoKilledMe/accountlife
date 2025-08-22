package com.acco.life.common;

/**
 * 系统常量类
 *
 * @author wensen.zhang
 * @version V1.0.0
 */
public class Constants {
    
    /**
     * 默认页码
     */
    public static final int DEFAULT_PAGE_NUMBER = 0;
    
    /**
     * 默认页大小
     */
    public static final int DEFAULT_PAGE_SIZE = 20;
    
    /**
     * 最大页大小
     */
    public static final int MAX_PAGE_SIZE = 100;
    
    /**
     * 删除标记 - 未删除
     */
    public static final int NOT_DELETED = 0;
    
    /**
     * 删除标记 - 已删除
     */
    public static final int DELETED = 1;
    
    /**
     * 默认用户角色
     */
    public static final String DEFAULT_USER_ROLE = "USER";
    
    /**
     * 管理员角色
     */
    public static final String ADMIN_ROLE = "ADMIN";
    
    /**
     * 日期时间格式
     */
    public static final String DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";
    
    /**
     * 日期格式
     */
    public static final String DATE_FORMAT = "yyyy-MM-dd";
    
    /**
     * 时间格式
     */
    public static final String TIME_FORMAT = "HH:mm:ss";
    
    /**
     * 金额精度
     */
    public static final int MONEY_SCALE = 2;
    
    /**
     * 默认货币符号
     */
    public static final String DEFAULT_CURRENCY = "CNY";
    
    /**
     * 文件上传最大大小（10MB）
     */
    public static final long MAX_FILE_SIZE = 10 * 1024 * 1024;
    
    /**
     * 支持的文件类型
     */
    public static final String[] SUPPORTED_FILE_TYPES = {
        ".csv", ".xlsx", ".xls", ".txt"
    };
    
    /**
     * 缓存过期时间（分钟）
     */
    public static final int CACHE_EXPIRE_MINUTES = 30;
    
    /**
     * 用户会话超时时间（分钟）
     */
    public static final int SESSION_TIMEOUT_MINUTES = 60;
} 