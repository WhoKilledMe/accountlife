package com.acco.life.enums.fin;

import lombok.Getter;

/**
 * 账户所有者类型枚举
 *
 * @author wensen.zhang
 * @version V2.0.0
 */
@Getter
public enum OwnerType {
    
    USER("USER", "用户账户"),
    PLATFORM("PLATFORM", "平台账户"),
    MERCHANT("MERCHANT", "商户账户"),
    SYSTEM("SYSTEM", "系统账户");

    private final String code;
    private final String name;

    OwnerType(String code, String name) {
        this.code = code;
        this.name = name;
    }

    public static OwnerType fromCode(String code) {
        for (OwnerType type : values()) {
            if (type.getCode().equals(code)) {
                return type;
            }
        }
        return null;
    }
}
