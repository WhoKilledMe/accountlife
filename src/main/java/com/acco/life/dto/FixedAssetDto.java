package com.acco.life.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * description: 固定资产数据传输对象，用于封装固定资产相关数据
 *
 * @date: 2025-07-24 17:54:23
 * @author wensen.zhang
 * @version V1.0.0
 */
@Data
@Schema(name = "FixedAssetDto", description = "FixedAsset 数据传输对象")
public class FixedAssetDto {

    @Schema(name = "id", description = "固定资产ID", accessMode = Schema.AccessMode.READ_ONLY)
    private Integer id;

    @Schema(name = "userId", description = "所属用户", accessMode = Schema.AccessMode.READ_ONLY)
    private Integer userId;

    @Schema(name = "name", description = "资产名称")
    private String name;

    @Schema(name = "type", description = "资产类型：1-property，2-vehicle，3-equipment，4-other")
    private Integer type;

    @Schema(name = "value", description = "评估/购入金额")
    private BigDecimal value;

    @Schema(name = "purchaseDate", description = "购买日期")
    private LocalDateTime purchaseDate;

    @Schema(name = "location", description = "地址或位置")
    private String location;

    @Schema(name = "note", description = "备注")
    private String note;

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