package com.acco.life.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Schema(name = "AssetAccountDto", description = "AssetAccount 数据传输对象")
public class AssetAccountDto {

    @Schema(name = "id", description = "账户ID")
    private Integer id;
    @Schema(name = "userId", description = "所属用户")
    private Integer userId;
    @Schema(name = "name", description = "账户名称（如 招商银行、花呗）")
    private String name;
    @Schema(name = "type", description = "账户类型：1-bank(银行)，2-platform(平台)，3-credit_wallet(信用钱包)，4-wallet(虚拟余额)")
    private Integer type;
    @Schema(name = "platformCode", description = "平台标识（如 ALIPAY、MEITUAN）")
    private String platformCode;
    @Schema(name = "accountNumber", description = "银行卡号/平台账号")
    private String accountNumber;
    @Schema(name = "isVirtual", description = "是否为虚拟账户（如信用钱包）")
    private String isVirtual;
    @Schema(name = "creditLimit", description = "信用额度，仅信用钱包用")
    private BigDecimal creditLimit;
    @Schema(name = "currency", description = "币种")
    private String currency;
    @Schema(name = "createdAt", description = "创建时间")
    private LocalDateTime createdAt;
}
