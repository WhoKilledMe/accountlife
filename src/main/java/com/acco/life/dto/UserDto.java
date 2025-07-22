package com.acco.life.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Schema(name = "UserDto", description = "UserAccount 数据传输对象")
public class UserDto {

    @Schema(name = "id", description = "用户ID")
    private Integer id;

    @Schema(name = "username", description = "用户名")
    private String username;

    @Schema(name = "email", description = "邮箱")
    private String email;

    @Schema(name = "phone", description = "手机号")
    private String phone;

    @Schema(name = "createdAt", description = "创建时间")
    private LocalDateTime createdAt;

    @Schema(name = "groupId", description = "用户组Id")
    private Integer groupId ;

    @Schema(name = "role", description = "用户组角色")
    private Integer role ;

}
