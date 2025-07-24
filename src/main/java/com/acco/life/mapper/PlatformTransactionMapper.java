package com.acco.life.mapper;

import com.acco.life.dto.PlatformTransactionDto;
import com.acco.life.entity.PlatformTransaction;
import org.mapstruct.Mapper;

/**
 * description: 平台交易映射器，负责DTO与实体的转换
 *
 * @date: 2025-07-24 17:54:23
 * @author wensen.zhang
 * @version V1.0.0
 */
@Mapper(componentModel = "spring")
public interface PlatformTransactionMapper {

    PlatformTransaction toEntity(PlatformTransactionDto dto);

    PlatformTransactionDto toDto(PlatformTransaction entity);

}
