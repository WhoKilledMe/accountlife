package com.acco.life.mapper;

import com.acco.life.dto.UserGroupDto;
import com.acco.life.entity.UserGroup;
import org.mapstruct.Mapper;

/**
 * description: 用户组映射器，负责DTO与实体的转换
 *
 * @date: 2025-07-24 17:54:23
 * @author wensen.zhang
 * @version V1.0.0
 */
@Mapper(componentModel = "spring")
public interface UserGroupMapper {

    UserGroup toEntity(UserGroupDto dto);

    UserGroupDto toDto(UserGroup entity);

}
