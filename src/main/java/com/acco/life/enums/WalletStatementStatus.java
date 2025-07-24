package com.acco.life.enums;

/**
 * description: 钱包账单状态枚举，定义了账单的常见状态
 *
 * @date: 2025-07-24 17:54:23
 * @author wensen.zhang
 * @version V1.0.0
 */
public enum WalletStatementStatus {
    OPEN(1, "未还"),
    PAID(2, "已还"),
    OVERDUE(3, "逾期");

    public final int code;
    public final String name;

    WalletStatementStatus(int code, String name) {
        this.code = code;
        this.name = name;
    }
}
