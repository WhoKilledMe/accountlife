package com.acco.life.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;

import java.time.LocalDateTime;

/**
 * description: [此处简要描述文件功能]
 *
 * @author wensen.zhang
 * @version V1.0.0
 * @date: 2025-09-01 16:42:04
 */
@Data
public class BaseColumnEntity {

    /**
     * 用户组ID
     */
    @Id
    @Column("id")
    private Integer id;

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
