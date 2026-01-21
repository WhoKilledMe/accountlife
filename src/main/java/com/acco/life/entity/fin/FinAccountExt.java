package com.acco.life.entity.fin;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

/**
 * 账户扩展信息实体类
 * 按 account_id 一对一或一对多存放银行/券商等扩展信息
 *
 * @author wensen.zhang
 * @version V2.0.0
 */
@Table("fin_account_ext")
@Data
public class FinAccountExt {

    /**
     * 扩展ID
     */
    @Id
    @Column("id")
    private Long id;

    /**
     * fin_account.id
     */
    @Column("account_id")
    private Long accountId;

    /**
     * 银行/券商等扩展信息（卡号掩码、账单日、开户行、授信额度等）
     * JSON格式存储
     */
    @Column("json_ext")
    private String jsonExt;

    /**
     * 创建时间
     */
    @Column("created_at")
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    @Column("updated_at")
    private LocalDateTime updatedAt;
}
