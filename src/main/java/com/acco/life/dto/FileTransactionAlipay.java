package com.acco.life.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 支付宝账单 CSV DTO
 *
 * @author wensen.zhang
 */
@Data
@EqualsAndHashCode(callSuper = true)
@JsonPropertyOrder({
        "tradeTime", "tradeCategory", "counterparty", "counterpartyAccount", "productDescription",
        "incomeOrExpense", "amount", "paymentMethod", "tradeStatus", "tradeOrderNo", "merchantOrderNo", "remark"
})
public class FileTransactionAlipay extends FileTransactionDto {

    @JsonAlias({"交易时间"})
    private String tradeTime;

    @JsonAlias({"交易分类"})
    private String tradeCategory;

    @JsonAlias({"交易对方"})
    private String counterparty;

    @JsonAlias({"对方账号"})
    private String counterpartyAccount;

    @JsonAlias({"商品说明"})
    private String productDescription;

    @JsonAlias({"收/支"})
    private String incomeOrExpense; // 收入/支出/不计收支

    @JsonAlias({"金额"})
    private String amount; // e.g. 16.89

    @JsonAlias({"收/付款方式"})
    private String paymentMethod; // e.g. 账户余额/花呗/宁波银行信用卡(4573)

    @JsonAlias({"交易状态"})
    private String tradeStatus; // e.g. 交易成功/等待确认收货

    @JsonAlias({"交易订单号"})
    private String tradeOrderNo;

    @JsonAlias({"商家订单号"})
    private String merchantOrderNo;

    @JsonAlias({"备注"})
    private String remark;
}
