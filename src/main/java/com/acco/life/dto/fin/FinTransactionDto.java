package com.acco.life.dto.fin;

import com.acco.life.dto.BaseColumnDto;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 交易中枢 DTO
 *
 * @author wensen.zhang
 * @version V2.0.0
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class FinTransactionDto extends BaseColumnDto {

    private Long userId;
    private String transactionNo;
    private String bizType;
    private String bizSubType;
    private Long categoryId;
    private LocalDateTime tradeTime;
    private BigDecimal amount;
    private String currency;
    private String status;
    private String counterparty;
    private String platformCode;
    private Long originalTransactionId;
    private String remark;

    // 扩展字段
    private String bizTypeName;
    private String categoryName;
    private String platformName;
    private String statusName;
    
    // 关联的支付路由
    private List<FinPaymentRouteDto> paymentRoutes;
    
    // 关联的账单映射
    private List<FinStatementDto> statements;
}
