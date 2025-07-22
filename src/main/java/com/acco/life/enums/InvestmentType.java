package com.acco.life.enums;


public enum InvestmentType {
    STOCK(1, "股票"),
    FUND(2, "基金"),
    BOND(3, "债券");

    public final int code;
    public final String name;

    InvestmentType(int code, String name) {
        this.code = code;
        this.name = name;
    }
}
