package com.acco.life.mapper.fin;

import com.acco.life.dto.fin.FinTransactionDto;
import com.acco.life.entity.fin.FinTransaction;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

/**
 * 交易中枢 Mapper
 *
 * @author wensen.zhang
 * @version V2.0.0
 */
@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface FinTransactionMapper {

    FinTransactionDto toDto(FinTransaction entity);

    FinTransaction toEntity(FinTransactionDto dto);

    void updateEntityFromDto(FinTransactionDto dto, @MappingTarget FinTransaction entity);
}
