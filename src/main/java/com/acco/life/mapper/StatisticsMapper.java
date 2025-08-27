package com.acco.life.mapper;

import com.acco.life.dto.StatisticsDto;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface StatisticsMapper {
    StatisticsMapper INSTANCE = Mappers.getMapper(StatisticsMapper.class);
    // 可根据需要添加实体与DTO的转换方法
}