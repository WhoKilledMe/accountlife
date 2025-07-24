package com.acco.life.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Schema(name = "InvestmentAssetDto", description = "InvestmentAsset 数据传输对象")
public class InvestmentAssetDto {

    @Schema(name = "id", description = "投资资产ID")
    private Integer id;

    @Schema(name = "userId", description = "所属用户", accessMode = Schema.AccessMode.READ_ONLY)
    private Integer userId;

    @Schema(name = "accountId", description = "关联账户")
    private Integer accountId;

    @Schema(name = "code", description = "股票/基金代码")
    private String code;

    @Schema(name = "name", description = "名称")
    private String name;

    @Schema(name = "type", description = "类型：1-stock，2-fund，3-bond")
    private Integer type;

    @Schema(name = "quantity", description = "持仓数量")
    private BigDecimal quantity;

    @Schema(name = "costPrice", description = "成本价")
    private BigDecimal costPrice;

    @Schema(name = "marketPrice", description = "市价")
    private BigDecimal marketPrice;

    @Schema(name = "currency", description = "币种")
    private String currency;

    @Schema(name = "lastUpdated", description = "更新时间")
    private LocalDateTime lastUpdated;

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