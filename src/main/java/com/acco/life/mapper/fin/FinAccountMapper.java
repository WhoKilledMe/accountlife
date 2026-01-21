package com.acco.life.mapper.fin;

import com.acco.life.dto.fin.FinAccountDto;
import com.acco.life.entity.fin.FinAccount;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

/**
 * 账户主表 Mapper
 *
 * @author wensen.zhang
 * @version V2.0.0
 */
@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface FinAccountMapper {

    FinAccountDto toDto(FinAccount entity);

    FinAccount toEntity(FinAccountDto dto);

    void updateEntityFromDto(FinAccountDto dto, @MappingTarget FinAccount entity);
}
