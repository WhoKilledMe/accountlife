package com.acco.life.enums;

/**
 * description: 固定资产类型枚举，定义了常见的固定资产类型
 *
 * @date: 2025-07-24 17:54:23
 * @author wensen.zhang
 * @version V1.0.0
 */
public enum FixedAssetType {
    PROPERTY(1, "房产"),
    VEHICLE(2, "车辆"),
    EQUIPMENT(3, "设备"),
    OTHER(4, "其他");

    public final int code;
    public final String name;

    FixedAssetType(int code, String name) {
        this.code = code;
        this.name = name;
    }
}
