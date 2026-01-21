package com.acco.life.mapper.fin;

import com.acco.life.dto.fin.FinClearingFlowDto;
import com.acco.life.entity.fin.FinClearingFlow;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

/**
 * 清算流水 Mapper
 *
 * @author wensen.zhang
 * @version V2.0.0
 */
@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface FinClearingFlowMapper {

    FinClearingFlowDto toDto(FinClearingFlow entity);

    FinClearingFlow toEntity(FinClearingFlowDto dto);

    void updateEntityFromDto(FinClearingFlowDto dto, @MappingTarget FinClearingFlow entity);
}
