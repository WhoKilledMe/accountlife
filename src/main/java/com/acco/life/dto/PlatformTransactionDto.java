package com.acco.life.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * description: 平台交易数据传输对象，用于封装平台账单相关数据
 *
 * @date: 2025-07-24 17:54:23
 * @author wensen.zhang
 * @version V1.0.0
 */
@Data
@Schema(name = "PlatformTransactionDto", description = "PlatformTransaction 数据传输对象")
public class PlatformTransactionDto {

    @Schema(name = "id", description = "平台账单ID", accessMode = Schema.AccessMode.READ_ONLY)
    private Integer id;

    @Schema(name = "userId", description = "所属用户", accessMode = Schema.AccessMode.READ_ONLY)
    private Integer userId;

    @Schema(name = "platformCode", description = "平台编码")
    private String platformCode;

    @Schema(name = "rawJson", description = "原始账单JSON数据")
    private String rawJson;

    @Schema(name = "mappedTransactionId", description = "映射到业务交易ID")
    private Integer mappedTransactionId;

    @Schema(name = "remark", description = "备注")
    private String remark;

    @Schema(name = "createdBy", description = "创建人", accessMode = Schema.AccessMode.READ_ONLY)
    private String createdBy;

    @Schema(name = "createdAt", description = "导入时间", accessMode = Schema.AccessMode.READ_ONLY)
    private LocalDateTime createdAt;

    @Schema(name = "updatedBy", description = "修改人", accessMode = Schema.AccessMode.READ_ONLY)
    private String updatedBy;

    @Schema(name = "updatedAt", description = "修改时间", accessMode = Schema.AccessMode.READ_ONLY)
    private LocalDateTime updatedAt;

    @Schema(name = "isDeleted", description = "是否删除（0-否，1-是）", accessMode = Schema.AccessMode.READ_ONLY)
    private Integer isDeleted;

}