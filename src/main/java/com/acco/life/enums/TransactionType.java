package com.acco.life.enums;

/**
 * description: 交易类型枚举，定义了收入、支出和转账等类型
 *
 * @date: 2025-07-24 17:54:23
 * @author wensen.zhang
 * @version V1.0.0
 */
public enum TransactionType {
    INCOME(1, "收入"),
    EXPENSE(2, "支出"),
    TRANSFER_OUT(3, "转出"),
    TRANSFER_IN(4, "转入");

    public final int code;
    public final String name;

    TransactionType(int code, String name) {
        this.code = code;
        this.name = name;
    }
    
    /**
     * 根据代码获取交易类型
     */
    public static TransactionType getByCode(int code) {
        for (TransactionType type : values()) {
            if (type.code == code) {
                return type;
            }
        }
        return null;
    }
    
    /**
     * 根据名称获取交易类型
     */
    public static TransactionType getByName(String name) {
        for (TransactionType type : values()) {
            if (type.name.equals(name)) {
                return type;
            }
        }
        return null;
    }
    
    /**
     * 判断是否为收入类型
     */
    public boolean isIncome() {
        return this == INCOME;
    }
    
    /**
     * 判断是否为支出类型
     */
    public boolean isExpense() {
        return this == EXPENSE;
    }
    
    /**
     * 判断是否为转账类型
     */
    public boolean isTransfer() {
        return this == TRANSFER_OUT || this == TRANSFER_IN;
    }
}
