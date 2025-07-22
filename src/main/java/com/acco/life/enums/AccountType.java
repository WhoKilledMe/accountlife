package com.acco.life.enums;


public enum AccountType {
    BANK(1, "银行"),
    PLATFORM(2, "平台"),
    CREDIT_WALLET(3, "信用钱包"),
    WALLET(4, "虚拟余额");

    public final int code;
    public final String name;

    AccountType(int code, String name) {
        this.code = code;
        this.name = name;
    }
}
