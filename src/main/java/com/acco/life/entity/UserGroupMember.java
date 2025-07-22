package com.acco.life.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

@Table("user_group_member")
@Data
public class UserGroupMember {


    /**
     * 关系ID
     */
    @Id
    @Column("id")
    private Integer id;

    /**
     * 所属用户组ID
     */
    
    @Column("group_id")
    private Integer groupId;

    /**
     * 用户ID
     */
    
    @Column("user_id")
    private Integer userId;

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
