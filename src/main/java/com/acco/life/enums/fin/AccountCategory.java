package com.acco.life.enums.fin;

import lombok.Getter;

/**
 * 账户大类枚举
 *
 * @author wensen.zhang
 * @version V2.0.0
 */
@Getter
public enum AccountCategory {
    
    CASH("CASH", "现金类账户", "银行卡、电子钱包等可直接支付的账户"),
    CLEARING("CLEARING", "清算账户", "用于资金清算的过渡账户"),
    INVEST("INVEST", "投资账户", "券商、基金等投资类账户"),
    BENEFIT("BENEFIT", "权益账户", "优惠券、红包、积分等权益类账户"),
    TRANSIT("TRANSIT", "在途账户", "资金在途、未到账的临时账户"),
    LEDGER("LEDGER", "记账账户", "仅用于记账统计的虚拟账户");

    private final String code;
    private final String name;
    private final String description;

    AccountCategory(String code, String name, String description) {
        this.code = code;
        this.name = name;
        this.description = description;
    }

    public static AccountCategory fromCode(String code) {
        for (AccountCategory category : values()) {
            if (category.getCode().equals(code)) {
                return category;
            }
        }
        return null;
    }
}
