package com.acco.life.dto.fin;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 账单-账户映射 DTO
 *
 * @author wensen.zhang
 * @version V2.0.0
 */
@Data
public class FinStatementAccountMapDto {

    private Long id;
    private Long statementId;
    private Long accountId;
    private String mapType;
    private BigDecimal confidence;
    private String mappedBy;
    private LocalDateTime mappedAt;
    private String remark;

    // 扩展字段
    private String accountName;
    private String mapTypeName;
}
