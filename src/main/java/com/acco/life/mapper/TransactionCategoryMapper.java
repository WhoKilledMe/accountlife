package com.acco.life.mapper;

import com.acco.life.dto.TransactionCategoryDto;
import com.acco.life.entity.TransactionCategory;
import org.mapstruct.Mapper;

/**
 * description: 交易分类映射器，负责DTO与实体的转换
 *
 * @date: 2025-07-24 17:54:23
 * @author wensen.zhang
 * @version V1.0.0
 */
@Mapper(componentModel = "spring")
public interface TransactionCategoryMapper {

    TransactionCategory toEntity(TransactionCategoryDto dto);

    TransactionCategoryDto toDto(TransactionCategory entity);

}
