package com.acco.life.mapper.fin;

import com.acco.life.dto.fin.FinAccountFlowDto;
import com.acco.life.entity.fin.FinAccountFlow;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

/**
 * 总账流水 Mapper
 *
 * @author wensen.zhang
 * @version V2.0.0
 */
@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface FinAccountFlowMapper {

    FinAccountFlowDto toDto(FinAccountFlow entity);

    FinAccountFlow toEntity(FinAccountFlowDto dto);

    void updateEntityFromDto(FinAccountFlowDto dto, @MappingTarget FinAccountFlow entity);
}
