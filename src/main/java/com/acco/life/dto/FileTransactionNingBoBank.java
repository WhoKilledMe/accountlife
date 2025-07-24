package com.acco.life.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Data;

/**
 * description: [此处简要描述文件功能]
 *
 * @author wensen.zhang
 * @version V1.0.0
 * @date: 2025-07-22 19:48:55
 */
@Data
@JsonPropertyOrder({ "transactionDate", "accountingDate", "transactionSummary","transactionAmount" })
public class FileTransactionNingBoBank extends FileTransactionDto {

    @JsonAlias({"交易日期"})
    private String transactionDate;

    @JsonAlias({"记账日期"})
    private String accountingDate;

    @JsonAlias({"交易摘要"})
    private String transactionSummary;

    @JsonAlias({"交易金额"})
    private String transactionAmount;
}
