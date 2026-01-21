package com.acco.life.dto.fin;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 账户扩展信息 DTO
 *
 * @author wensen.zhang
 * @version V2.0.0
 */
@Data
public class FinAccountExtDto {

    private Long id;
    private Long accountId;
    private String jsonExt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
