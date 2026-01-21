package com.acco.life.dto.fin;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 账户余额快照 DTO
 *
 * @author wensen.zhang
 * @version V2.0.0
 */
@Data
public class FinAccountBalanceSnapshotDto {

    private Long id;
    private Long accountId;
    private LocalDate snapshotDate;
    private BigDecimal balance;
    private String currency;
    private LocalDateTime createdAt;

    // 扩展字段
    private String accountName;
}
