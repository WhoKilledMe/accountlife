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
}
