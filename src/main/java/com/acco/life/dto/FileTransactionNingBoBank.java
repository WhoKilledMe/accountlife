package com.acco.life.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Data;

/**
 * description: 宁波银行账单文件交易DTO，继承自FileTransactionDto
 *
 * @date: 2025-07-24 17:54:23
 * @author wensen.zhang
 * @version V1.0.0
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

    public void setTransactionAmount(String transactionAmount) {
        if (transactionAmount != null && !transactionAmount.isBlank()) {
            try {
                // 转成 BigDecimal 取相反数再转回字符串
                this.transactionAmount = new java.math.BigDecimal(transactionAmount)
                        .negate()
                        .toPlainString();
            } catch (NumberFormatException e) {
                this.transactionAmount = transactionAmount;
            }
        } else {
            this.transactionAmount = transactionAmount;
        }
    }
}
