package com.acco.life.entity.fin;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

/**
 * 账单文件导入日志实体类
 * 记录文件来源及处理状态
 *
 * @author wensen.zhang
 * @version V2.0.0
 */
@Table("fin_statement_file")
@Data
public class FinStatementFile {

    /**
     * 文件导入记录ID
     */
    @Id
    @Column("id")
    private Long id;

    /**
     * 所属用户（可为空表示系统/公共文件）
     */
    @Column("user_id")
    private Long userId;

    /**
     * 文件来源平台：WECHAT/ALIPAY/CMB/MEITUAN/TIKTOK/EXTERNAL
     */
    @Column("platform_code")
    private String platformCode;

    /**
     * 上传文件名
     */
    @Column("file_name")
    private String fileName;

    /**
     * 文件类型：CSV/ZIP/JSON/PDF/EMAIL_ATTACHMENT
     */
    @Column("file_type")
    private String fileType;

    /**
     * 文件 md5（用于幂等）
     */
    @Column("file_md5")
    private String fileMd5;

    /**
     * 总行数（估计）
     */
    @Column("total_rows")
    private Integer totalRows;

    /**
     * 成功解析行数
     */
    @Column("success_count")
    private Integer successCount;

    /**
     * 失败行数
     */
    @Column("failure_count")
    private Integer failureCount;

    /**
     * PENDING/PROCESSING/COMPLETED/FAILED
     */
    @Column("status")
    private String status;

    /**
     * 导入渠道：FILE_UPLOAD/EMAIL/API/FTP/THIRD_PARTY
     */
    @Column("source_channel")
    private String sourceChannel;

    /**
     * 上传时间
     */
    @Column("uploaded_at")
    private LocalDateTime uploadedAt;

    /**
     * 处理完成时间
     */
    @Column("processed_at")
    private LocalDateTime processedAt;

    /**
     * 解析错误日志（如解析失败原因）
     */
    @Column("error_log")
    private String errorLog;

    /**
     * 创建人
     */
    @Column("created_by")
    private String createdBy;

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
