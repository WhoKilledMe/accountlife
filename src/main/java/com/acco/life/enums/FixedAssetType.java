package com.acco.life.enums;


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
