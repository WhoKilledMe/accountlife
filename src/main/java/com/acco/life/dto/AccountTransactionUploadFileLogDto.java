package com.acco.life.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * description: 交易导入文件日志DTO
 *
 * @author wensen.zhang
 * @version V1.0.0
 * @date: 2025-07-24 17:54:23
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Schema(name = "AccountTransactionUploadFileLogDto", description = "交易导入文件日志DTO")
public class AccountTransactionUploadFileLogDto extends BaseColumnDto {

    @Schema(name = "userId", description = "用户ID")
    private Long userId;

    @Schema(name = "userName", description = "用户名", accessMode = Schema.AccessMode.READ_ONLY)
    private String userName;

    @Schema(name = "accountId", description = "账户ID")
    private Long accountId;

    @Schema(name = "accountName", description = "账户名称", accessMode = Schema.AccessMode.READ_ONLY)
    private String accountName;

    @Schema(name = "fileName", description = "文件名称")
    private String fileName;

    @Schema(name = "filePath", description = "文件路径")
    private String filePath;

    @Schema(name = "zipPassword", description = "压缩文件密码（如果有）")
    private Integer zipPassword;

    @Schema(name = "transactionStartDate", description = "交易开始日期")
    private LocalDateTime transactionStartDate;

    @Schema(name = "transactionEndDate", description = "交易结束日期")
    private LocalDateTime transactionEndDate;

    @Schema(name = "md5Checksum", description = "文件MD5校验码")
    private String md5Checksum;

    @Schema(name = "status", description = "处理状态：PENDING, PROCESSING, COMPLETED, FAILED")
    private String status;

    @Schema(name = "totalRecords", description = "总记录数")
    private Integer totalRecords;

    @Schema(name = "successCount", description = "成功导入记录数")
    private Integer successCount;

    @Schema(name = "failureCount", description = "失败记录数")
    private Integer failureCount;

    @Schema(name = "errorMessage", description = "错误信息（如果有）")
    private String errorMessage;
}


