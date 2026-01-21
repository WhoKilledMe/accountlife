package com.acco.life.entity.fin;

import com.acco.life.entity.BaseColumnEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 账户主表实体类
 * 描述资金容器（银行/钱包/券商/补贴等）
 *
 * @author wensen.zhang
 * @version V2.0.0
 */
@EqualsAndHashCode(callSuper = true)
@Table("fin_account")
@Data
public class FinAccount extends BaseColumnEntity {

    /**
     * 所属用户ID
     */
    @Column("user_id")
    private Long userId;

    /**
     * 系统内部唯一编码（格式：ACCT-{userId}-{seq}）
     */
    @Column("account_code")
    private String accountCode;

    /**
     * 账户可读名，如：宁波信用卡(尾号1234)、微信余额
     */
    @Column("account_name")
    private String accountName;

    /**
     * 账户大类：CASH/CLEARING/INVEST/BENEFIT/TRANSIT/LEDGER
     */
    @Column("account_category")
    private String accountCategory;

    /**
     * 细分类：BANK_CARD/E_WALLET/CREDIT/BROKER_FUND/FUND_TA/STOCK_POSITION/COUPON/SUBSIDY
     */
    @Column("account_type")
    private String accountType;

    /**
     * 账户所有者类型：USER/PLATFORM/MERCHANT/SYSTEM
     */
    @Column("owner_type")
    private String ownerType;

    /**
     * 外部机构/平台代码：WECHAT/ALIPAY/MEITUAN/CMB/TONGHUASHUN 等
     */
    @Column("platform_code")
    private String platformCode;

    /**
     * 币种
     */
    @Column("currency")
    private String currency;

    /**
     * 当前余额（冗余字段，由流水同步）
     */
    @Column("balance")
    private BigDecimal balance;

    /**
     * 余额最后更新时间
     */
    @Column("balance_updated_at")
    private LocalDateTime balanceUpdatedAt;

    /**
     * 外部账户标识（卡号尾号/账号/OpenID等，用于去重）
     */
    @Column("external_account_ref")
    private String externalAccountRef;

    /**
     * 是否虚拟账户（1=虚拟，如补贴池；0=真实银行/券商）
     */
    @Column("is_virtual")
    private Boolean isVirtual;

    /**
     * 状态：ACTIVE/INACTIVE/FROZEN
     */
    @Column("status")
    private String status;

    /**
     * 备注字段
     */
    @Column("remark")
    private String remark;
}
