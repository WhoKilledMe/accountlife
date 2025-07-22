package com.acco.life.enums;


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
