package com.acco.life.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Schema(name = "UserGroupMemberDto", description = "UserGroupMember 数据传输对象")
public class UserGroupMemberDto {

    @Schema(name = "id", description = "关系ID")
    private Integer id;
    @Schema(name = "groupId", description = "所属用户组ID")
    private Integer groupId;
    @Schema(name = "userId", description = "用户ID")
    private Integer userId;
    @Schema(name = "role", description = "角色权限：1-组长(owner)，2-成员(member)")
    private Integer role;
    @Schema(name = "joinedAt", description = "加入时间")
    private LocalDateTime joinedAt;
}
