package com.acco.life.enums;

/**
 * description: 投资类型枚举，定义了常见的投资类型
 *
 * @date: 2025-07-24 17:54:23
 * @author wensen.zhang
 * @version V1.0.0
 */
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
