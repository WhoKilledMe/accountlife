package com.acco.life.mapper;

import com.acco.life.dto.UserDto;
import com.acco.life.entity.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {

    User toEntity(UserDto dto);

    UserDto toDto(User entity, Integer groupId);

    UserDto toDto(User entity);

}
