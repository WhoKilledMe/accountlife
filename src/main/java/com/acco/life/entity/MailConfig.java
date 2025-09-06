package com.acco.life.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

/**
 * 邮件配置实体类
 *
 * @author wensen.zhang
 * @version V1.0.0
 */
@EqualsAndHashCode(callSuper = true)
@Table("mail_config")
@Data
public class MailConfig extends BaseColumnEntity {

    /**
     * 配置名称
     */
    @Column("name")
    private String name;

    /**
     * SMTP服务器地址
     */
    @Column("host")
    private String host;

    /**
     * SMTP端口
     */
    @Column("port")
    private Integer port;

    /**
     * 用户ID（关联用户表）
     */
    @Column("user_id")
    private Integer userId;

    /**
     * 邮箱账号
     */
    @Column("email_address")
    private String emailAddress;

    /**
     * 邮箱授权码
     */
    @Column("auth_code")
    private String authCode;

    /**
     * 是否启用SSL
     */
    @Column("enable_ssl")
    private Boolean enableSsl;

    /**
     * 是否启用TLS
     */
    @Column("enable_tls")
    private Boolean enableTls;

    /**
     * 连接超时时间（毫秒）
     */
    @Column("connection_timeout")
    private Integer connectionTimeout;

    /**
     * 读取超时时间（毫秒）
     */
    @Column("read_timeout")
    private Integer readTimeout;

    /**
     * 是否启用
     */
    @Column("is_active")
    private Boolean isActive;

    /**
     * 描述信息
     */
    @Column("description")
    private String description;
}
