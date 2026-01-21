package com.acco.life.mapper.fin;

import com.acco.life.dto.fin.FinTransactionStatementMapDto;
import com.acco.life.entity.fin.FinTransactionStatementMap;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

/**
 * 交易-账单映射 Mapper
 *
 * @author wensen.zhang
 * @version V2.0.0
 */
@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface FinTransactionStatementMapMapper {

    FinTransactionStatementMapDto toDto(FinTransactionStatementMap entity);

    FinTransactionStatementMap toEntity(FinTransactionStatementMapDto dto);

    void updateEntityFromDto(FinTransactionStatementMapDto dto, @MappingTarget FinTransactionStatementMap entity);
}
