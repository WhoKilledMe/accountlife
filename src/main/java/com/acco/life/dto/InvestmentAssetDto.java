package com.acco.life.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * description: 投资资产数据传输对象，用于封装投资资产相关数据
 *
 * @date: 2025-07-24 17:54:23
 * @author wensen.zhang
 * @version V1.0.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Schema(name = "InvestmentAssetDto", description = "InvestmentAsset 数据传输对象")
public class InvestmentAssetDto extends BaseColumnDto {

    @Schema(name = "userId", description = "所属用户", accessMode = Schema.AccessMode.READ_ONLY)
    private Long userId;

    @Schema(name = "accountId", description = "关联账户")
    private Long accountId;

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
}