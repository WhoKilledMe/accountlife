package com.acco.life.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

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
@EqualsAndHashCode(callSuper = true)
@Schema(name = "FixedAssetDto", description = "FixedAsset 数据传输对象")
public class FixedAssetDto extends BaseColumnDto {

    @Schema(name = "userId", description = "所属用户", accessMode = Schema.AccessMode.READ_ONLY)
    private Long userId;

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
}