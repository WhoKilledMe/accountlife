package com.acco.life.enums.fin;

import lombok.Getter;

/**
 * 会计流水方向枚举（复式记账）
 *
 * @author wensen.zhang
 * @version V2.0.0
 */
@Getter
public enum FlowDirection {
    
    DEBIT("DEBIT", "借方", "资产增加或负债减少"),
    CREDIT("CREDIT", "贷方", "资产减少或负债增加"),
    IN("IN", "流入", "资金流入"),
    OUT("OUT", "流出", "资金流出");

    private final String code;
    private final String name;
    private final String description;

    FlowDirection(String code, String name, String description) {
        this.code = code;
        this.name = name;
        this.description = description;
    }

    public static FlowDirection fromCode(String code) {
        for (FlowDirection direction : values()) {
            if (direction.getCode().equals(code)) {
                return direction;
            }
        }
        return null;
    }
}
