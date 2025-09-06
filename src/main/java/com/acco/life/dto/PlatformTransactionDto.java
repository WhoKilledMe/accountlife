package com.acco.life.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * description: 平台交易数据传输对象，用于封装平台账单相关数据
 *
 * @date: 2025-07-24 17:54:23
 * @author wensen.zhang
 * @version V1.0.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Schema(name = "PlatformTransactionDto", description = "PlatformTransaction 数据传输对象")
public class PlatformTransactionDto extends BaseColumnDto {

    @Schema(name = "userId", description = "所属用户", accessMode = Schema.AccessMode.READ_ONLY)
    private Long userId;

    @Schema(name = "platformCode", description = "平台编码")
    private String platformCode;

    @Schema(name = "rawJson", description = "原始账单JSON数据")
    private String rawJson;

    @Schema(name = "mappedTransactionId", description = "映射到业务交易ID")
    private Long mappedTransactionId;

    @Schema(name = "remark", description = "备注")
    private String remark;
}