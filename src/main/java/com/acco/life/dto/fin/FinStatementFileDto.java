package com.acco.life.dto.fin;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 账单文件导入日志 DTO
 *
 * @author wensen.zhang
 * @version V2.0.0
 */
@Data
public class FinStatementFileDto {

    private Long id;
    private Long userId;
    private String platformCode;
    private String fileName;
    private String fileType;
    private String fileMd5;
    private Integer totalRows;
    private Integer successCount;
    private Integer failureCount;
    private String status;
    private String sourceChannel;
    private LocalDateTime uploadedAt;
    private LocalDateTime processedAt;
    private String errorLog;
    private String createdBy;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // 扩展字段
    private String platformName;
    private String statusName;
}
