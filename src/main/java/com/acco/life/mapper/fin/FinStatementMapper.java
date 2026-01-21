package com.acco.life.mapper.fin;

import com.acco.life.dto.fin.FinStatementDto;
import com.acco.life.entity.fin.FinStatement;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

/**
 * 账单行 Mapper
 *
 * @author wensen.zhang
 * @version V2.0.0
 */
@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface FinStatementMapper {

    FinStatementDto toDto(FinStatement entity);

    FinStatement toEntity(FinStatementDto dto);

    void updateEntityFromDto(FinStatementDto dto, @MappingTarget FinStatement entity);
}
