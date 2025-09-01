package com.acco.life.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

/**
 * description: 平台交易实体类，对应数据库表platform_transaction
 *
 * @date: 2025-07-24 17:54:23
 * @author wensen.zhang
 * @version V1.0.0
 */
@EqualsAndHashCode(callSuper = true)
@Table("platform_transaction")
@Data
public class PlatformTransaction extends BaseColumnEntity {

    /**
     * 所属用户
     */
    @Column("user_id")
    private Integer userId;

    /**
     * 平台编码
     */
    @Column("platform_code")
    private String platformCode;

    /**
     * 原始账单JSON数据
     */
    @Column("raw_json")
    private String rawJson;

    /**
     * 映射到业务交易ID
     */
    @Column("mapped_transaction_id")
    private Integer mappedTransactionId;

    /**
     * 备注
     */
    @Column("remark")
    private String remark;
}
