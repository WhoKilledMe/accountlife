package com.acco.life.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 美团账单 CSV DTO
 */
@Data
@EqualsAndHashCode(callSuper = true)
@JsonPropertyOrder({
        "createdTime", "successTime", "transactionType", "orderTitle", "incomeOrExpense",
        "paymentMethod", "orderAmount", "paidAmount", "tradeNo", "merchantNo", "remark"
})
public class FileTransactionMeituan extends FileTransactionDto {

    @JsonAlias({"交易创建时间"})
    private String createdTime;

    @JsonAlias({"交易成功时间"})
    private String successTime;

    @JsonAlias({"交易类型"})
    private String transactionType;

    @JsonAlias({"订单标题"})
    private String orderTitle;

    @JsonAlias({"收/支"})
    private String incomeOrExpense; // 支出/收入

    @JsonAlias({"支付方式"})
    private String paymentMethod;

    @JsonAlias({"订单金额"})
    private String orderAmount; // e.g. ¥18.00

    @JsonAlias({"实付金额"})
    private String paidAmount; // e.g. ¥18.00

    @JsonAlias({"交易单号"})
    private String tradeNo;

    @JsonAlias({"商家单号"})
    private String merchantNo;

    @JsonAlias({"备注"})
    private String remark;
}


