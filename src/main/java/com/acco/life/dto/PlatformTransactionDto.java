package com.acco.life.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Schema(name = "PlatformTransactionDto", description = "PlatformTransaction 数据传输对象")
public class PlatformTransactionDto {

    @Schema(name = "id", description = "平台账单ID")
    private Integer id;
    @Schema(name = "userId", description = "所属用户")
    private Integer userId;
    @Schema(name = "platformCode", description = "平台编码（如 ALIPAY）")
    private String platformCode;
    @Schema(name = "rawJson", description = "原始账单JSON数据")
    private String rawJson;
    @Schema(name = "mappedTransactionId", description = "映射到业务交易ID")
    private Integer mappedTransactionId;
    @Schema(name = "remark", description = "备注")
    private String remark;
    @Schema(name = "createdAt", description = "导入时间")
    private LocalDateTime createdAt;
}
