package com.acco.life.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * description: 用户组成员数据传输对象，用于封装用户组成员相关数据
 *
 * @date: 2025-07-24 17:54:23
 * @author wensen.zhang
 * @version V1.0.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Schema(name = "UserGroupMemberDto", description = "UserGroupMember 数据传输对象")
public class UserGroupMemberDto extends BaseColumnDto {

    @Schema(name = "groupId", description = "所属用户组ID")
    private Integer groupId;

    @Schema(name = "userId", description = "用户ID")
    private Integer userId;

    @Schema(name = "role", description = "角色权限：1-组长(owner)，2-成员(member)")
    private Integer role;

    @Schema(name = "joinedAt", description = "加入时间")
    private LocalDateTime joinedAt;
}