package com.acco.life.mapper;

import com.acco.life.dto.UserDto;
import com.acco.life.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {

    User toEntity(UserDto dto);

    UserDto toDto(User entity, Integer groupId);

    @Mapping(source = "id", target = "id")
    UserDto dtoToDto(UserDto entity,Integer id, Integer groupId, Integer role);

    UserDto toDto(User entity);

}
