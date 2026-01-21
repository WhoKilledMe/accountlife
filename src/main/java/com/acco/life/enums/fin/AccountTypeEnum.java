package com.acco.life.enums.fin;

import lombok.Getter;

/**
 * 账户细分类型枚举
 *
 * @author wensen.zhang
 * @version V2.0.0
 */
@Getter
public enum AccountTypeEnum {
    
    // 现金类
    BANK_CARD("BANK_CARD", "银行卡", AccountCategory.CASH),
    E_WALLET("E_WALLET", "电子钱包", AccountCategory.CASH),
    CREDIT("CREDIT", "信用账户", AccountCategory.CASH),
    
    // 投资类
    BROKER_FUND("BROKER_FUND", "券商资金账户", AccountCategory.INVEST),
    FUND_TA("FUND_TA", "基金TA账户", AccountCategory.INVEST),
    STOCK_POSITION("STOCK_POSITION", "股票持仓", AccountCategory.INVEST),
    FUND_POSITION("FUND_POSITION", "基金持仓", AccountCategory.INVEST),
    
    // 权益类
    COUPON("COUPON", "优惠券", AccountCategory.BENEFIT),
    SUBSIDY("SUBSIDY", "补贴/红包", AccountCategory.BENEFIT),
    POINTS("POINTS", "积分", AccountCategory.BENEFIT),
    
    // 清算/在途类
    CLEARING_ACCOUNT("CLEARING_ACCOUNT", "清算账户", AccountCategory.CLEARING),
    TRANSIT_ACCOUNT("TRANSIT_ACCOUNT", "在途账户", AccountCategory.TRANSIT),
    
    // 记账类
    VIRTUAL_LEDGER("VIRTUAL_LEDGER", "虚拟记账", AccountCategory.LEDGER);

    private final String code;
    private final String name;
    private final AccountCategory category;

    AccountTypeEnum(String code, String name, AccountCategory category) {
        this.code = code;
        this.name = name;
        this.category = category;
    }

    public static AccountTypeEnum fromCode(String code) {
        for (AccountTypeEnum type : values()) {
            if (type.getCode().equals(code)) {
                return type;
            }
        }
        return null;
    }
}
