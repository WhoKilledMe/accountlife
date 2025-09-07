package com.acco.life.mapper;

import com.acco.life.dto.BusinessTransactionDto;
import com.acco.life.entity.BusinessTransaction;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * description: 业务交易映射器，负责DTO与实体的转换
 *
 * @date: 2025-01-15 10:00:00
 * @author wensen.zhang
 * @version V1.0.0
 */
@Mapper(componentModel = "spring")
public interface BusinessTransactionMapper {

    BusinessTransaction toEntity(BusinessTransactionDto dto);

    @Mapping(target = "categoryName", ignore = true)
    BusinessTransactionDto toDto(BusinessTransaction entity);
}
