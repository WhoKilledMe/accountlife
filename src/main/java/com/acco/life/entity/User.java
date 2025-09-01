package com.acco.life.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

/**
 * description: 用户实体类，对应数据库表user
 *
 * @date: 2025-07-24 17:54:23
 * @author wensen.zhang
 * @version V1.0.0
 */
@EqualsAndHashCode(callSuper = true)
@Table("user")
@Data
public class User extends BaseColumnEntity {

    /**
     * 用户名
     */
    @Column("username")
    private String username;

    /**
     * 邮箱
     */
    @Column("email")
    private String email;

    /**
     * 手机号
     */
    @Column("phone")
    private String phone;

    /**
     * 加盐MD5密文，格式：salt:md5
     */
    @Column("password_hash")
    private String password;
}