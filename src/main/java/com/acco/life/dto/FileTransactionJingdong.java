package com.acco.life.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 京东账单 CSV DTO
 */
@Data
@EqualsAndHashCode(callSuper = true)
@JsonPropertyOrder({
        "tradeTime", "merchantName", "tradeDescription", "amount", "paymentMethod",
        "tradeStatus", "incomeOrExpense", "tradeCategory", "tradeOrderNo", "merchantOrderNo", "remark"
})
public class FileTransactionJingdong extends FileTransactionDto {

    @JsonAlias({"交易时间"})
    private String tradeTime;

    @JsonAlias({"商户名称"})
    private String merchantName;

    @JsonAlias({"交易说明"})
    private String tradeDescription;

    @JsonAlias({"金额"})
    private String amount;

    @JsonAlias({"收/付款方式"})
    private String paymentMethod;

    @JsonAlias({"交易状态"})
    private String tradeStatus;

    @JsonAlias({"收/支"})
    private String incomeOrExpense;

    @JsonAlias({"交易分类"})
    private String tradeCategory;

    @JsonAlias({"交易订单号"})
    private String tradeOrderNo;

    @JsonAlias({"商家订单号"})
    private String merchantOrderNo;

    @JsonAlias({"备注"})
    private String remark;
}
