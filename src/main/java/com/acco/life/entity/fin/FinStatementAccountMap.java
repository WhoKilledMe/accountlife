package com.acco.life.entity.fin;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 账单到账户的映射表实体类
 * 用于确定哪条账单属于哪个账户
 *
 * @author wensen.zhang
 * @version V2.0.0
 */
@Table("fin_statement_account_map")
@Data
public class FinStatementAccountMap {

    /**
     * 映射ID
     */
    @Id
    @Column("id")
    private Long id;

    /**
     * fin_statement.id
     */
    @Column("statement_id")
    private Long statementId;

    /**
     * fin_account.id
     */
    @Column("account_id")
    private Long accountId;

    /**
     * AUTO/MANUAL
     */
    @Column("map_type")
    private String mapType;

    /**
     * 匹配置信度 0-100
     */
    @Column("confidence")
    private BigDecimal confidence;

    /**
     * 映射操作人/系统标识
     */
    @Column("mapped_by")
    private String mappedBy;

    /**
     * 映射时间
     */
    @Column("mapped_at")
    private LocalDateTime mappedAt;

    /**
     * 备注
     */
    @Column("remark")
    private String remark;
}
