package com.acco.life.enums;


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
