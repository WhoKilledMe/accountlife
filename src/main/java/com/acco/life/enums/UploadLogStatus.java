package com.acco.life.enums;


/**
 * 上传日志状态枚举
 */
public enum UploadLogStatus implements EnumInterface {
    PENDING("PENDING", "待处理"),
    PROCESSING("PROCESSING", "处理中"),
    COMPLETED("COMPLETED", "完成"),
    FAILED("FAILED", "失败");

    private final String code;
    private final String name;

    UploadLogStatus(String code, String name) {
        this.code = code;
        this.name = name;
    }

    @Override
    public String getCode() {
        return code;
    }

    @Override
    public String getName() {
        return name;
    }

}
