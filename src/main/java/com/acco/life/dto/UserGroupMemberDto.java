package com.acco.life.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * description: 用户组成员数据传输对象，用于封装用户组成员相关数据
 *
 * @date: 2025-07-24 17:54:23
 * @author wensen.zhang
 * @version V1.0.0
 */
@Data
@Schema(name = "UserGroupMemberDto", description = "UserGroupMember 数据传输对象")
public class UserGroupMemberDto {

    @Schema(name = "id", description = "关系ID", accessMode = Schema.AccessMode.READ_ONLY)
    private Integer id;

    @Schema(name = "groupId", description = "所属用户组ID")
    private Integer groupId;

    @Schema(name = "userId", description = "用户ID")
    private Integer userId;

    @Schema(name = "role", description = "角色权限：1-组长(owner)，2-成员(member)")
    private Integer role;

    @Schema(name = "joinedAt", description = "加入时间")
    private LocalDateTime joinedAt;

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

}