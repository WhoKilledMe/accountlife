package com.acco.life.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 账户配置DTO
 *
 * @author wensen.zhang
 * @version V1.0.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Schema(name = "AccountConfigDto", description = "AccountConfig 数据传输对象")
public class AccountConfigDto extends BaseColumnDto {

    @Schema(name = "name", description = "账户名称（如 招商银行、支付宝）")
    private String name;

    @Schema(name = "type", description = "账户类型：1-bank，2-platform，3-credit_wallet，4-wallet，5-insurance，6-securities")
    private Integer type;

    @Schema(name = "platformCode", description = "平台标识（如 ALIPAY、MEITUAN）")
    private String platformCode;

    @Schema(name = "websiteUrl", description = "官网登录地址")
    private String websiteUrl;

    @Schema(name = "description", description = "描述信息")
    private String description;

    @Schema(name = "logoUrl", description = "Logo图片地址")
    private String logoUrl;

    @Schema(name = "sortOrder", description = "排序权重")
    private Integer sortOrder;

    @Schema(name = "isActive", description = "是否启用")
    private Boolean isActive;
}
