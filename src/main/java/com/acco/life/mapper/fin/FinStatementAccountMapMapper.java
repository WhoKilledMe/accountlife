package com.acco.life.mapper.fin;

import com.acco.life.dto.fin.FinStatementAccountMapDto;
import com.acco.life.entity.fin.FinStatementAccountMap;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

/**
 * 账单-账户映射 Mapper
 *
 * @author wensen.zhang
 * @version V2.0.0
 */
@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface FinStatementAccountMapMapper {

    FinStatementAccountMapDto toDto(FinStatementAccountMap entity);

    FinStatementAccountMap toEntity(FinStatementAccountMapDto dto);

    void updateEntityFromDto(FinStatementAccountMapDto dto, @MappingTarget FinStatementAccountMap entity);
}
