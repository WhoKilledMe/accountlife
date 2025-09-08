package com.acco.life.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@EqualsAndHashCode(callSuper = true)
@Table("user_mail_config")
@Data
public class UserMailConfig extends BaseColumnEntity {

    @Column("user_id")
    private Long userId;

    @Column("name")
    private String name;

    @Column("host")
    private String host;

    @Column("port")
    private Integer port;

    @Column("email_address")
    private String emailAddress;

    @Column("auth_code")
    private String authCode;

    @Column("enable_ssl")
    private Boolean enableSsl;

    @Column("enable_tls")
    private Boolean enableTls;

    @Column("connection_timeout")
    private Integer connectionTimeout;

    @Column("read_timeout")
    private Integer readTimeout;

    @Column("is_active")
    private Boolean isActive;

    @Column("description")
    private String description;
}


