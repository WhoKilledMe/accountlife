package com.acco.life.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * description: 基础列数据传输对象，包含所有实体共有的字段
 *
 * @author wensen.zhang
 * @version V1.0.0
 * @date: 2025-01-01 00:00:00
 */
@Data
public class BaseColumnDto {

    /**
     * 主键ID
     */
    @Schema(name = "id", description = "主键ID", accessMode = Schema.AccessMode.READ_ONLY)
    private Integer id;

    /**
     * 创建人
     */
    @Schema(name = "createdBy", description = "创建人", accessMode = Schema.AccessMode.READ_ONLY)
    private String createdBy;

    /**
     * 创建时间
     */
    @Schema(name = "createdAt", description = "创建时间", accessMode = Schema.AccessMode.READ_ONLY)
    private LocalDateTime createdAt;

    /**
     * 修改人
     */
    @Schema(name = "updatedBy", description = "修改人", accessMode = Schema.AccessMode.READ_ONLY)
    private String updatedBy;

    /**
     * 修改时间
     */
    @Schema(name = "updatedAt", description = "修改时间", accessMode = Schema.AccessMode.READ_ONLY)
    private LocalDateTime updatedAt;

    /**
     * 是否删除（0-否，1-是）
     */
    @Schema(name = "isDeleted", description = "是否删除（0-否，1-是）", accessMode = Schema.AccessMode.READ_ONLY)
    private Integer isDeleted;
}
