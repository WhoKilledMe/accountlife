package com.acco.life.enums;

/**
 * description: 账户类型枚举，定义了系统支持的账户类型
 *
 * @date: 2025-07-24 17:54:23
 * @author wensen.zhang
 * @version V1.0.0
 */
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