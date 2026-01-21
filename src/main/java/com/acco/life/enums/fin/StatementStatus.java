package com.acco.life.enums.fin;

import lombok.Getter;

/**
 * 账单状态枚举
 *
 * @author wensen.zhang
 * @version V2.0.0
 */
@Getter
public enum StatementStatus {
    
    NEW("NEW", "新导入", "刚导入尚未处理"),
    PARSED("PARSED", "已解析", "已完成解析但未映射"),
    MAPPED("MAPPED", "已映射", "已映射到账户和交易"),
    IGNORED("IGNORED", "已忽略", "被标记为忽略");

    private final String code;
    private final String name;
    private final String description;

    StatementStatus(String code, String name, String description) {
        this.code = code;
        this.name = name;
        this.description = description;
    }

    public static StatementStatus fromCode(String code) {
        for (StatementStatus status : values()) {
            if (status.getCode().equals(code)) {
                return status;
            }
        }
        return null;
    }
}
