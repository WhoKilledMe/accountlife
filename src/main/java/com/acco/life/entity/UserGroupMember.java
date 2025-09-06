package com.acco.life.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

/**
 * description: 用户组成员实体类，对应数据库表user_group_member
 *
 * @date: 2025-07-24 17:54:23
 * @author wensen.zhang
 * @version V1.0.0
 */
@EqualsAndHashCode(callSuper = true)
@Table("user_group_member")
@Data
public class UserGroupMember extends BaseColumnEntity {

    /**
     * 所属用户组ID
     */
    @Column("group_id")
    private Long groupId;

    /**
     * 用户ID
     */
    @Column("user_id")
    private Long userId;

    /**
     * 角色权限：1-组长(owner)，2-成员(member)
     */
    @Column("role")
    private Integer role;

    /**
     * 加入时间
     */
    @Column("joined_at")
    private LocalDateTime joinedAt;
}
