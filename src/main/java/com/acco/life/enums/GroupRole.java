package com.acco.life.enums;

/**
 * description: 用户组角色枚举，定义了组长和成员两种角色
 *
 * @date: 2025-07-24 17:54:23
 * @author wensen.zhang
 * @version V1.0.0
 */
public enum GroupRole {
    OWNER(1, "组长"),
    MEMBER(2, "成员");

    public final int code;
    public final String name;

    GroupRole(int code, String name) {
        this.code = code;
        this.name = name;
    }
}
