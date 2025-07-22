package com.acco.life.mapper;

import com.acco.life.dto.UserGroupDto;
import com.acco.life.entity.UserGroup;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserGroupMapper {

    UserGroup toEntity(UserGroupDto dto);

    UserGroupDto toDto(UserGroup entity);

}
