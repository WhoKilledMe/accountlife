package com.acco.life.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * description: 用户组数据传输对象，用于封装用户组相关数据
 *
 * @date: 2025-07-24 17:54:23
 * @author wensen.zhang
 * @version V1.0.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Schema(name = "UserGroupDto", description = "UserGroup 数据传输对象")
public class UserGroupDto extends BaseColumnDto {

    @Schema(name = "name", description = "组名称（如 家庭、公司账本）")
    private String name;

    @Schema(name = "description", description = "组描述")
    private String description;
}