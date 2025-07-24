package com.acco.life.enums;

import lombok.Getter;

@Getter
public enum TransactionSourceType {

    NING_BO_CREDIT(0, "宁波银行信用卡"),
    BANK(1, "银行"),
    PLATFORM(2, "平台"),
    CREDIT_WALLET(3, "信用钱包");

    public final int code;
    public final String name;

    TransactionSourceType(int code, String name) {
        this.code = code;
        this.name = name;
    }
}
