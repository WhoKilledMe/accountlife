package com.acco.life.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Schema(name = "FixedAssetDto", description = "FixedAsset 数据传输对象")
public class FixedAssetDto {

    @Schema(name = "id", description = "固定资产ID")
    private Integer id;
    @Schema(name = "userId", description = "所属用户")
    private Integer userId;
    @Schema(name = "name", description = "资产名称")
    private String name;
    @Schema(name = "type", description = "资产类型：1-property(房产)，2-vehicle(车辆)，3-equipment(设备)，4-other(其他)")
    private Integer type;
    @Schema(name = "value", description = "评估/购入金额")
    private BigDecimal value;
    @Schema(name = "purchaseDate", description = "购买日期")
    private LocalDateTime purchaseDate;
    @Schema(name = "location", description = "地址或位置")
    private String location;
    @Schema(name = "note", description = "备注")
    private String note;
    @Schema(name = "createdAt", description = "创建时间")
    private LocalDateTime createdAt;
}
