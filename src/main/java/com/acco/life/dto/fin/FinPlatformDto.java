package com.acco.life.dto.fin;

import com.acco.life.dto.BaseColumnDto;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 平台字典表 DTO
 *
 * @author wensen.zhang
 * @version V2.0.0
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class FinPlatformDto extends BaseColumnDto {

    private String platformCode;
    private String platformName;
    private String platformType;
    private Boolean supportPayment;
    private Boolean supportCredit;
    private Boolean supportBalance;
    private Boolean supportBill;
    private String billImportFormat;
    private String billEmailDomain;
    private String logoUrl;
    private String websiteUrl;
    private String apiConfig;
    private String description;
    private Integer sortOrder;
    private String status;

    // 扩展字段（展示用）
    private String platformTypeName;
}
