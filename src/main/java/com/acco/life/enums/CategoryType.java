package com.acco.life.enums;

/**
 * description: 分类类型枚举，定义了收入和支出两种类型
 *
 * @date: 2025-07-24 17:54:23
 * @author wensen.zhang
 * @version V1.0.0
 */
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
