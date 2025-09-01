package com.acco.life.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

/**
 * description: 用户组实体类，对应数据库表user_group
 *
 * @date: 2025-07-24 17:54:23
 * @author wensen.zhang
 * @version V1.0.0
 */
@EqualsAndHashCode(callSuper = true)
@Table("user_group")
@Data
public class UserGroup extends BaseColumnEntity {

    /**
     * 组名称（如 家庭、公司账本）
     */
    @Column("name")
    private String name;

    /**
     * 组描述
     */
    @Column("description")
    private String description;

}
