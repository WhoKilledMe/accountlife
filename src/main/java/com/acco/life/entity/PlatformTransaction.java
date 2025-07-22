package com.acco.life.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

@Table("platform_transaction")
@Data
public class PlatformTransaction {


    /**
     * 平台账单ID
     */
    @Id
    @Column("id")
    private Integer id;

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

    /**
     * 创建人
     */
    
    @Column("created_by")
    private String createdBy;

    /**
     * 导入时间
     */
    
    @Column("created_at")
    private LocalDateTime createdAt;

    /**
     * 修改人
     */
    
    @Column("updated_by")
    private String updatedBy;

    /**
     * 修改时间
     */
    
    @Column("updated_at")
    private LocalDateTime updatedAt;

    /**
     * 是否删除（0-否，1-是）
     */
    
    @Column("is_deleted")
    private Integer isDeleted;
}
