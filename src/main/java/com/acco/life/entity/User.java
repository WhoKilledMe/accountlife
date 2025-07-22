package com.acco.life.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

/**
 * @author wensenzhang
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
     * 创建时间
     */
    
    @Column("created_at")
    private LocalDateTime createdAt;
}
