package com.acco.life.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Schema(name = "TransactionCategoryDto", description = "TransactionCategory 数据传输对象")
public class TransactionCategoryDto {

    @Schema(name = "id", description = "分类ID")
    private Integer id;

    @Schema(name = "name", description = "分类名称（如 餐饮）")
    private String name;

    @Schema(name = "type", description = "分类类型：1-income，2-expense")
    private Integer type;

    @Schema(name = "parentId", description = "父分类ID")
    private Integer parentId;

    @Schema(name = "icon", description = "图标")
    private String icon;

    @Schema(name = "userId", description = "所属用户，null 表示系统分类")
    private Integer userId;

    @Schema(name = "sortOrder", description = "排序值")
    private Integer sortOrder;

    @Schema(name = "createdBy", description = "创建人")
    private String createdBy;

    @Schema(name = "createdAt", description = "创建时间")
    private LocalDateTime createdAt;

    @Schema(name = "updatedBy", description = "修改人")
    private String updatedBy;

    @Schema(name = "updatedAt", description = "修改时间")
    private LocalDateTime updatedAt;

    @Schema(name = "isDeleted", description = "是否删除（0-否，1-是）")
    private Integer isDeleted;

}
