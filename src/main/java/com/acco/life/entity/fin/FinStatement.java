package com.acco.life.entity.fin;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 账单行表实体类
 * 保存每一行原始/解析后数据，作为证据
 *
 * @author wensen.zhang
 * @version V2.0.0
 */
@Table("fin_statement")
@Data
public class FinStatement {

    /**
     * 账单行ID
     */
    @Id
    @Column("id")
    private Long id;

    /**
     * fin_statement_file.id
     */
    @Column("file_id")
    private Long fileId;

    /**
     * 所属用户
     */
    @Column("user_id")
    private Long userId;

    /**
     * 来源平台：WECHAT/ALIPAY/CMB/MEITUAN/TIKTOK/OTHER
     */
    @Column("platform_code")
    private String platformCode;

    /**
     * 细分类：PLATFORM_ORDER/BANK_STATEMENT/CREDIT_CARD_STATEMENT/SECURITIES_TRADE
     */
    @Column("source_type")
    private String sourceType;

    /**
     * 行级幂等Hash（md5(file_id + row_raw)）
     */
    @Column("raw_row_hash")
    private String rawRowHash;

    /**
     * 原始或解析后的行 JSON（完整）
     */
    @Column("raw_data")
    private String rawData;

    /**
     * 平台订单号/流水号（若有）
     */
    @Column("out_trade_no")
    private String outTradeNo;

    /**
     * 账单行时间（原始时间）
     */
    @Column("stmt_time")
    private LocalDateTime stmtTime;

    /**
     * 金额
     */
    @Column("amount")
    private BigDecimal amount;

    /**
     * IN/OUT
     */
    @Column("direction")
    private String direction;

    /**
     * 对方/商户（美团/拼多多/招商银行）
     */
    @Column("counterparty")
    private String counterparty;

    /**
     * 原始文本中指示的账户（用于初步映射，如 卡号尾号/支付宝账号）
     */
    @Column("account_ref")
    private String accountRef;

    /**
     * 交易摘要/备注
     */
    @Column("description")
    private String description;

    /**
     * 交易分类ID（关联 transaction_category）
     */
    @Column("category_id")
    private Long categoryId;

    /**
     * 解析器版本号（便于重解析）
     */
    @Column("parser_version")
    private String parserVersion;

    /**
     * 重试次数
     */
    @Column("retry_count")
    private Integer retryCount;

    /**
     * 解析完成时间
     */
    @Column("parsed_at")
    private LocalDateTime parsedAt;

    /**
     * NEW/PARSED/MAPPED/IGNORED
     */
    @Column("status")
    private String status;

    /**
     * 导入时间
     */
    @Column("created_at")
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    @Column("updated_at")
    private LocalDateTime updatedAt;
}
