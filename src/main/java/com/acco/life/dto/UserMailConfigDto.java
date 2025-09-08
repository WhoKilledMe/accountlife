package com.acco.life.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@Schema(name = "UserMailConfigDto", description = "用户邮箱配置DTO")
public class UserMailConfigDto extends BaseColumnDto {

    @Schema(name = "userId", description = "用户ID")
    private Long userId;

    @Schema(name = "name", description = "配置名称")
    private String name;

    @Schema(name = "host", description = "SMTP服务器地址")
    private String host;

    @Schema(name = "port", description = "SMTP端口")
    private Integer port;

    @Schema(name = "emailAddress", description = "邮箱账号")
    private String emailAddress;

    @Schema(name = "authCode", description = "邮箱授权码")
    private String authCode;

    @Schema(name = "enableSsl", description = "是否启用SSL")
    private Boolean enableSsl;

    @Schema(name = "enableTls", description = "是否启用TLS")
    private Boolean enableTls;

    @Schema(name = "connectionTimeout", description = "连接超时时间（毫秒）")
    private Integer connectionTimeout;

    @Schema(name = "readTimeout", description = "读取超时时间（毫秒）")
    private Integer readTimeout;

    @Schema(name = "isActive", description = "是否启用")
    private Boolean isActive;

    @Schema(name = "description", description = "描述信息")
    private String description;
}


