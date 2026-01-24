package com.acco.life.entity.fin;

import com.acco.life.entity.BaseColumnEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

/**
 * 账户扩展信息实体类
 * 按 account_id 一对一或一对多存放银行/券商等扩展信息
 *
 * @author wensen.zhang
 * @version V2.0.0
 */
@EqualsAndHashCode(callSuper = true)
@Table("fin_account_ext")
@Data
public class FinAccountExt extends BaseColumnEntity {

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
}
