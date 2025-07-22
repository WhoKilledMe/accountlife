package com.acco.life.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(name = "TransactionCategoryDto", description = "TransactionCategory 数据传输对象")
public class TransactionCategoryDto {

    @Schema(name = "id", description = "分类ID")
    private Integer id;
    @Schema(name = "name", description = "分类名称（如 餐饮）")
    private String name;
    @Schema(name = "type", description = "分类类型：1-income(收入)，2-expense(支出)")
    private Integer type;
    @Schema(name = "parentId", description = "父分类ID")
    private Integer parentId;
    @Schema(name = "icon", description = "图标")
    private String icon;
    @Schema(name = "userId", description = "所属用户，null 表示系统分类")
    private Integer userId;
    @Schema(name = "sortOrder", description = "排序值")
    private Integer sortOrder;
}
