package com.acco.life.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 微信账单 CSV DTO
 */
@Data
@EqualsAndHashCode(callSuper = true)
@JsonPropertyOrder({
        "tradeTime", "tradeType", "counterparty", "product", "incomeOrExpense",
        "amount", "paymentMethod", "currentStatus", "tradeNo", "merchantNo", "remark"
})
public class FileTransactionWechat extends FileTransactionDto {

    @JsonAlias({"交易时间"})
    private String tradeTime;

    @JsonAlias({"交易类型"})
    private String tradeType;

    @JsonAlias({"交易对方"})
    private String counterparty;

    @JsonAlias({"商品"})
    private String product;

    @JsonAlias({"收/支"})
    private String incomeOrExpense;

    @JsonAlias({"金额(元)"})
    private String amount;

    @JsonAlias({"支付方式"})
    private String paymentMethod;

    @JsonAlias({"当前状态"})
    private String currentStatus;

    @JsonAlias({"交易单号"})
    private String tradeNo;

    @JsonAlias({"商户单号"})
    private String merchantNo;

    @JsonAlias({"备注"})
    private String remark;
}
