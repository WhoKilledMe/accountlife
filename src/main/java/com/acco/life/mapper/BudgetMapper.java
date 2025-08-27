package com.acco.life.mapper;

import com.acco.life.dto.BudgetDto;
import com.acco.life.entity.Budget;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface BudgetMapper {
    BudgetMapper INSTANCE = Mappers.getMapper(BudgetMapper.class);

    BudgetDto toDto(Budget entity);
    Budget toEntity(BudgetDto dto);
}