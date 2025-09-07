package com.acco.life.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

/**
 * description: 交易导入文件日志实体，映射表 account_transaction_upload_file_log
 *
 * @author wensen.zhang
 * @version V1.0.0
 * @date: 2025-07-24 17:54:23
 */
@EqualsAndHashCode(callSuper = true)
@Table("account_transaction_upload_file_log")
@Data
public class AccountTransactionUploadFileLog extends BaseColumnEntity {

    /**
     * 用户ID（关联用户表）
     */
    @Column("user_id")
    private Long userId;

    /**
     * 账户ID（关联账户表）
     */
    @Column("account_id")
    private Long accountId;

    /**
     * 文件名称
     */
    @Column("file_name")
    private String fileName;

    /**
     * 文件路径
     */
    @Column("file_path")
    private String filePath;

    /**
     * 压缩文件密码（如果有）
     */
    @Column("zip_password")
    private Integer zipPassword;

    /**
     * 交易开始日期
     */
    @Column("transaction_start_date")
    private LocalDateTime transactionStartDate;

    /**
     * 交易结束日期
     */
    @Column("transaction_end_date")
    private LocalDateTime transactionEndDate;

    /**
     * 文件MD5校验码
     */
    @Column("md5_checksum")
    private String md5Checksum;

    /**
     * 处理状态：PENDING, PROCESSING, COMPLETED, FAILED
     */
    @Column("status")
    private String status;

    /**
     * 总记录数
     */
    @Column("total_records")
    private Integer totalRecords;

    /**
     * 成功导入记录数
     */
    @Column("success_count")
    private Integer successCount;

    /**
     * 失败记录数
     */
    @Column("failure_count")
    private Integer failureCount;

    /**
     * 错误信息（如果有）
     */
    @Column("error_message")
    private String errorMessage;
}


