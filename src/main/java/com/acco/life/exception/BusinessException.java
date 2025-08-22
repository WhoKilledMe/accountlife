package com.acco.life.exception;

/**
 * 业务异常类
 *
 * @author wensen.zhang
 * @version V1.0.0
 */
public class BusinessException extends RuntimeException {

    public BusinessException(String message) {
        super(message);
    }

    public BusinessException(String message, Throwable cause) {
        super(message, cause);
    }
} 