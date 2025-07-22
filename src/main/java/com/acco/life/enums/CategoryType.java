package com.acco.life.enums;


public enum CategoryType {
    INCOME(1, "收入"),
    EXPENSE(2, "支出");

    public final int code;
    public final String name;

    CategoryType(int code, String name) {
        this.code = code;
        this.name = name;
    }
}
