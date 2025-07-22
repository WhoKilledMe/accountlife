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

    /**
     * 创建人
     */
    
    @Column("created_by")
    private String createdBy;

    /**
     * 创建时间
     */
    
    @Column("created_at")
    private LocalDateTime createdAt;

    /**
     * 修改人
     */
    
    @Column("updated_by")
    private String updatedBy;

    /**
     * 修改时间
     */
    
    @Column("updated_at")
    private LocalDateTime updatedAt;

    /**
     * 是否删除（0-否，1-是）
     */
    
    @Column("is_deleted")
    private Integer isDeleted;
}
