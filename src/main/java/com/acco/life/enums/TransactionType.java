package com.acco.life.enums;


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
