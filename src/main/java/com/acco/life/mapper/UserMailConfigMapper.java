package com.acco.life.mapper;

import com.acco.life.dto.UserMailConfigDto;
import com.acco.life.entity.UserMailConfig;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMailConfigMapper {
    UserMailConfig toEntity(UserMailConfigDto dto);
    UserMailConfigDto toDto(UserMailConfig entity);
}


