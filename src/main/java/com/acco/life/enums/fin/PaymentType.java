package com.acco.life.enums.fin;

import lombok.Getter;

/**
 * 支付类型枚举
 *
 * @author wensen.zhang
 * @version V2.0.0
 */
@Getter
public enum PaymentType {
    
    PLATFORM("PLATFORM", "平台支付", "微信支付、支付宝等聚合支付"),
    BALANCE("BALANCE", "余额支付", "使用账户余额支付"),
    CREDIT("CREDIT", "信用支付", "信用卡、花呗等信用类支付"),
    BANK("BANK", "银行支付", "银行卡直接支付"),
    MIXED("MIXED", "组合支付", "多种支付方式组合"),
    SUBSIDY("SUBSIDY", "补贴支付", "红包、优惠券等抵扣");

    private final String code;
    private final String name;
    private final String description;

    PaymentType(String code, String name, String description) {
        this.code = code;
        this.name = name;
        this.description = description;
    }

    public static PaymentType fromCode(String code) {
        for (PaymentType type : values()) {
            if (type.getCode().equals(code)) {
                return type;
            }
        }
        return null;
    }
}
