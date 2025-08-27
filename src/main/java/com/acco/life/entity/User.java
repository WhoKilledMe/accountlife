package com.acco.life.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

/**
 * description: 用户实体类，对应数据库表user
 *
 * @date: 2025-07-24 17:54:23
 * @author wensen.zhang
 * @version V1.0.0
 */
@Table("user")
@Data
public class User {


    /**
     * 用户ID
     */
    @Id
    @Column("id")
    private Integer id;

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

    /**
     * 创建人
     */
    @Column("created_by")
    private String createdBy;

    /**
     * 创建时间
     */
    @Column("created_at")
    private LocalDateTime createdAt;

    /**
     * 修改人
     */
    @Column("updated_by")
    private String updatedBy;

    /**
     * 修改时间
     */
    @Column("updated_at")
    private LocalDateTime updatedAt;

    /**
     * 是否删除（0-否，1-是）
     */
    @Column("is_deleted")
    private Integer isDeleted;
}