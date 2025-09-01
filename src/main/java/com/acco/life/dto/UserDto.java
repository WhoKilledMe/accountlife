package com.acco.life.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * description: 用户数据传输对象，用于在控制器层和服务层之间传递用户信息
 *
 * @date: 2025-07-24 17:54:23
 * @author wensen.zhang
 * @version V1.0.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Schema(name = "UserDto", description = "User 数据传输对象")
public class UserDto extends BaseColumnDto {

    @Schema(name = "username", description = "用户名")
    private String username;

    @Schema(name = "email", description = "邮箱")
    private String email;

    @Schema(name = "phone", description = "手机号")
    private String phone;

    @Schema(name = "password", description = "明文密码，仅创建或修改时传入", accessMode = Schema.AccessMode.WRITE_ONLY)
    private String password;

    @Schema(name = "groupId", description = "用户组Id")
    private Integer groupId;

    @Schema(name = "role", description = "用户组角色")
    private Integer role;
}