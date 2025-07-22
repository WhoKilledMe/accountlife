package com.acco.life.mapper;

import com.acco.life.dto.PlatformTransactionDto;
import com.acco.life.entity.PlatformTransaction;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PlatformTransactionMapper {

    PlatformTransaction toEntity(PlatformTransactionDto dto);

    PlatformTransactionDto toDto(PlatformTransaction entity);

}
