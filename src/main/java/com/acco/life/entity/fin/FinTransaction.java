package com.acco.life.entity.fin;

import com.acco.life.entity.BaseColumnEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 交易中枢表实体类
 * 表示一笔业务事实（消费/投资/还款等）
 *
 * @author wensen.zhang
 * @version V2.0.0
 */
@EqualsAndHashCode(callSuper = true)
@Table("fin_transaction")
@Data
public class FinTransaction extends BaseColumnEntity {

    /**
     * 所属用户
     */
    @Column("user_id")
    private Long userId;

    /**
     * 系统交易号/业务流水号
     */
    @Column("transaction_no")
    private String transactionNo;

    /**
     * 业务类型：PAY/TRANSFER/INVEST/REDEEM/REPAY/REFUND
     */
    @Column("biz_type")
    private String bizType;

    /**
     * 子类型，如：CREDIT_PAY/BALANCE_PAY/FUND_SUBSCRIBE/STOCK_BUY
     */
    @Column("biz_sub_type")
    private String bizSubType;

    /**
     * 交易分类ID（关联 transaction_category）
     */
    @Column("category_id")
    private Long categoryId;

    /**
     * 交易发生时间（业务时间）
     */
    @Column("trade_time")
    private LocalDateTime tradeTime;

    /**
     * 业务金额（通常为总额）
     */
    @Column("amount")
    private BigDecimal amount;

    /**
     * 币种
     */
    @Column("currency")
    private String currency;

    /**
     * 业务状态：INIT/MATCHED/CONFIRMED/CANCELLED
     */
    @Column("status")
    private String status;

    /**
     * 交易对手或商户名称（美团/拼多多/同花顺）
     */
    @Column("counterparty")
    private String counterparty;

    /**
     * 来源平台：MEITUAN/WECHAT/ALIPAY/TIKTOK/NINGBO_BANK
     */
    @Column("platform_code")
    private String platformCode;

    /**
     * 原交易ID（退款时指向原单）
     */
    @Column("original_transaction_id")
    private Long originalTransactionId;

    /**
     * 备注/摘要
     */
    @Column("remark")
    private String remark;
}
