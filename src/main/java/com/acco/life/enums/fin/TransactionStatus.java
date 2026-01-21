package com.acco.life.enums.fin;

import lombok.Getter;

/**
 * 交易状态枚举
 *
 * @author wensen.zhang
 * @version V2.0.0
 */
@Getter
public enum TransactionStatus {
    
    INIT("INIT", "初始化", "交易创建但未确认"),
    MATCHED("MATCHED", "已匹配", "已与账单匹配"),
    CONFIRMED("CONFIRMED", "已确认", "交易已确认完成"),
    CANCELLED("CANCELLED", "已取消", "交易已取消");

    private final String code;
    private final String name;
    private final String description;

    TransactionStatus(String code, String name, String description) {
        this.code = code;
        this.name = name;
        this.description = description;
    }

    public static TransactionStatus fromCode(String code) {
        for (TransactionStatus status : values()) {
            if (status.getCode().equals(code)) {
                return status;
            }
        }
        return null;
    }
}
