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
    @Schema(name = "userId", description = "所属用户")
    private Integer userId;
    @Schema(name = "accountId", description = "关联账户")
    private Integer accountId;
    @Schema(name = "code", description = "股票/基金代码")
    private String code;
    @Schema(name = "name", description = "名称")
    private String name;
    @Schema(name = "type", description = "类型：1-stock(股票)，2-fund(基金)，3-bond(债券)")
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
