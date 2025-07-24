package com.acco.life.mapper;

import com.acco.life.dto.UserDto;
import com.acco.life.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * description: 用户映射器，负责DTO与实体的转换
 *
 * @date: 2025-07-24 17:54:23
 * @author wensen.zhang
 * @version V1.0.0
 */
@Mapper(componentModel = "spring")
public interface UserMapper {

    User toEntity(UserDto dto);

    UserDto toDto(User entity, Integer groupId);

    @Mapping(source = "id", target = "id")
    UserDto dtoToDto(UserDto entity,Integer id, Integer groupId, Integer role);

    UserDto toDto(User entity);

}