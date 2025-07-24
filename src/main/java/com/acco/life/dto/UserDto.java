package com.acco.life.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Schema(name = "UserDto", description = "User 数据传输对象")
public class UserDto {

    @Schema(name = "id", description = "用户ID", accessMode = Schema.AccessMode.READ_ONLY)
    private Integer id;

    @Schema(name = "username", description = "用户名")
    private String username;

    @Schema(name = "email", description = "邮箱")
    private String email;

    @Schema(name = "phone", description = "手机号")
    private String phone;

    @Schema(name = "createdBy", description = "创建人", accessMode = Schema.AccessMode.READ_ONLY)
    private String createdBy;

    @Schema(name = "createdAt", description = "创建时间", accessMode = Schema.AccessMode.READ_ONLY)
    private LocalDateTime createdAt;

    @Schema(name = "updatedBy", description = "修改人", accessMode = Schema.AccessMode.READ_ONLY)
    private String updatedBy;

    @Schema(name = "updatedAt", description = "修改时间", accessMode = Schema.AccessMode.READ_ONLY)
    private LocalDateTime updatedAt;

    @Schema(name = "isDeleted", description = "是否删除（0-否，1-是）", accessMode = Schema.AccessMode.READ_ONLY)
    private Integer isDeleted;

    @Schema(name = "groupId", description = "用户组Id")
    private Integer groupId ;

    @Schema(name = "role", description = "用户组角色")
    private Integer role ;

}