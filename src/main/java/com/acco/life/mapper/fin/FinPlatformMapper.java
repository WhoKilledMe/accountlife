package com.acco.life.mapper.fin;

import com.acco.life.dto.fin.FinPlatformDto;
import com.acco.life.entity.fin.FinPlatform;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

/**
 * 平台字典表 Mapper
 *
 * @author wensen.zhang
 * @version V2.0.0
 */
@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface FinPlatformMapper {

    FinPlatformDto toDto(FinPlatform entity);

    FinPlatform toEntity(FinPlatformDto dto);

    void updateEntityFromDto(FinPlatformDto dto, @MappingTarget FinPlatform entity);
}
