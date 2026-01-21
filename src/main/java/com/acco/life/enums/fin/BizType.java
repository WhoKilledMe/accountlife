package com.acco.life.enums.fin;

import lombok.Getter;

/**
 * 业务类型枚举
 *
 * @author wensen.zhang
 * @version V2.0.0
 */
@Getter
public enum BizType {
    
    PAY("PAY", "消费支付", "日常消费、购物等支出"),
    TRANSFER("TRANSFER", "转账", "账户间转账"),
    INVEST("INVEST", "投资", "股票、基金等投资"),
    REDEEM("REDEEM", "赎回", "投资赎回"),
    REPAY("REPAY", "还款", "信用卡、贷款还款"),
    REFUND("REFUND", "退款", "交易退款"),
    INCOME("INCOME", "收入", "工资、奖金等收入"),
    WITHDRAW("WITHDRAW", "提现", "从平台提现到银行卡"),
    RECHARGE("RECHARGE", "充值", "充值到平台账户");

    private final String code;
    private final String name;
    private final String description;

    BizType(String code, String name, String description) {
        this.code = code;
        this.name = name;
        this.description = description;
    }

    public static BizType fromCode(String code) {
        for (BizType type : values()) {
            if (type.getCode().equals(code)) {
                return type;
            }
        }
        return null;
    }
}
