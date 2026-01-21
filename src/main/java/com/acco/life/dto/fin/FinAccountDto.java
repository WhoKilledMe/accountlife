package com.acco.life.dto.fin;

import com.acco.life.dto.BaseColumnDto;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 账户主表 DTO
 *
 * @author wensen.zhang
 * @version V2.0.0
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class FinAccountDto extends BaseColumnDto {

    private Long userId;
    private String accountCode;
    private String accountName;
    private String accountCategory;
    private String accountType;
    private String ownerType;
    private String platformCode;
    private String currency;
    private BigDecimal balance;
    private LocalDateTime balanceUpdatedAt;
    private String externalAccountRef;
    private Boolean isVirtual;
    private String status;
    private String remark;

    // 扩展字段（展示用）
    private String accountCategoryName;
    private String accountTypeName;
    private String platformName;
    
    // 关联的扩展信息
    private FinAccountExtDto accountExt;
}
