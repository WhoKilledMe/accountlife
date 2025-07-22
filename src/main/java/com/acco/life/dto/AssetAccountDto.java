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

    @Schema(name = "type", description = "账户类型：1-bank，2-platform，3-credit_wallet，4-wallet")
    private Integer type;

    @Schema(name = "platformCode", description = "平台标识（如 ALIPAY、MEITUAN）")
    private String platformCode;

    @Schema(name = "accountNumber", description = "银行卡号/平台账号")
    private String accountNumber;

    @Schema(name = "isVirtual", description = "是否为虚拟账户")
    private Boolean isVirtual;

    @Schema(name = "creditLimit", description = "信用额度，仅信用钱包用")
    private BigDecimal creditLimit;

    @Schema(name = "currency", description = "币种")
    private String currency;

    @Schema(name = "createdBy", description = "创建人")
    private String createdBy;

    @Schema(name = "createdAt", description = "创建时间")
    private LocalDateTime createdAt;

    @Schema(name = "updatedBy", description = "修改人")
    private String updatedBy;

    @Schema(name = "updatedAt", description = "修改时间")
    private LocalDateTime updatedAt;

    @Schema(name = "isDeleted", description = "是否删除（0-否，1-是）")
    private Integer isDeleted;

}
