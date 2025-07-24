package com.acco.life.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * description: 交易分类数据传输对象，用于封装交易分类相关数据
 *
 * @date: 2025-07-24 17:54:23
 * @author wensen.zhang
 * @version V1.0.0
 */
@Data
@Schema(name = "TransactionCategoryDto", description = "TransactionCategory 数据传输对象")
public class TransactionCategoryDto {

    @Schema(name = "id", description = "分类ID", accessMode = Schema.AccessMode.READ_ONLY)
    private Integer id;

    @Schema(name = "name", description = "分类名称（如 餐饮）")
    private String name;

    @Schema(name = "type", description = "分类类型：1-income，2-expense")
    private Integer type;

    @Schema(name = "parentId", description = "父分类ID")
    private Integer parentId;

    @Schema(name = "icon", description = "图标")
    private String icon;

    @Schema(name = "userId", description = "所属用户，null 表示系统分类", accessMode = Schema.AccessMode.READ_ONLY)
    private Integer userId;

    @Schema(name = "sortOrder", description = "排序值")
    private Integer sortOrder;

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