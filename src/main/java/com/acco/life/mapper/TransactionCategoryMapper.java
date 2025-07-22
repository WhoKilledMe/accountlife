package com.acco.life.mapper;

import com.acco.life.dto.TransactionCategoryDto;
import com.acco.life.entity.TransactionCategory;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface TransactionCategoryMapper {

    TransactionCategory toEntity(TransactionCategoryDto dto);

    TransactionCategoryDto toDto(TransactionCategory entity);

}
