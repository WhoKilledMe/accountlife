package com.acco.life.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Schema(name = "UserGroupDto", description = "UserGroup 数据传输对象")
public class UserGroupDto {

    @Schema(name = "id", description = "用户组ID")
    private Integer id;
    @Schema(name = "name", description = "组名称（如 家庭、公司账本）")
    private String name;
    @Schema(name = "description", description = "组描述")
    private String description;
    @Schema(name = "createdAt", description = "创建时间")
    private LocalDateTime createdAt;
}
