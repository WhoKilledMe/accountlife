package com.acco.life.enums.fin;

import lombok.Getter;

/**
 * 映射类型枚举
 *
 * @author wensen.zhang
 * @version V2.0.0
 */
@Getter
public enum MapType {
    
    // 账单-账户映射类型
    AUTO("AUTO", "自动映射", "系统自动匹配"),
    MANUAL("MANUAL", "手动映射", "人工手动匹配"),
    
    // 交易-账单映射类型
    ONE_TO_ONE("ONE_TO_ONE", "一对一", "一条账单对应一笔交易"),
    ONE_TO_MANY("ONE_TO_MANY", "一对多", "一笔交易对应多条账单（如分期）"),
    MANY_TO_ONE("MANY_TO_ONE", "多对一", "多条账单合并为一笔交易");

    private final String code;
    private final String name;
    private final String description;

    MapType(String code, String name, String description) {
        this.code = code;
        this.name = name;
        this.description = description;
    }

    public static MapType fromCode(String code) {
        for (MapType type : values()) {
            if (type.getCode().equals(code)) {
                return type;
            }
        }
        return null;
    }
}
