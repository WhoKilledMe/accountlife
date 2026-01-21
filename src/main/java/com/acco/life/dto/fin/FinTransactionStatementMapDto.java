package com.acco.life.dto.fin;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 交易-账单映射 DTO
 *
 * @author wensen.zhang
 * @version V2.0.0
 */
@Data
public class FinTransactionStatementMapDto {

    private Long id;
    private Long transactionId;
    private Long statementId;
    private String mapType;
    private BigDecimal allocatedAmount;
    private String confirmStatus;
    private BigDecimal confidence;
    private String matchRule;
    private String matchDetail;
    private LocalDateTime mappedAt;

    // 扩展字段
    private String mapTypeName;
    private String confirmStatusName;
}
