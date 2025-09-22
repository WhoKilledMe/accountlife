package com.acco.life.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

/**
 * description: 资产账户数据传输对象，用于封装资产账户相关数据
 *
 * @date: 2025-07-24 17:54:23
 * @author wensen.zhang
 * @version V1.0.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Schema(name = "UserAccountDto", description = "UserAccountDto 数据传输对象")
public class UserAccountDto extends BaseColumnDto {

    @Schema(name = "userId", description = "所属用户", accessMode = Schema.AccessMode.READ_ONLY)
    private Long userId;

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

    @Schema(name = "billEmail", description = "账单邮箱地址")
    private String billEmail;
}