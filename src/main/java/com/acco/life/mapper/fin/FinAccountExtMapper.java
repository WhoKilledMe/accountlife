package com.acco.life.mapper.fin;

import com.acco.life.dto.fin.FinAccountExtDto;
import com.acco.life.entity.fin.FinAccountExt;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

/**
 * 账户扩展信息 Mapper
 *
 * @author wensen.zhang
 * @version V2.0.0
 */
@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface FinAccountExtMapper {

    FinAccountExtDto toDto(FinAccountExt entity);

    FinAccountExt toEntity(FinAccountExtDto dto);

    void updateEntityFromDto(FinAccountExtDto dto, @MappingTarget FinAccountExt entity);
}
