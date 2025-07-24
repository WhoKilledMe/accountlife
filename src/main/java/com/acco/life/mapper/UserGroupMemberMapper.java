package com.acco.life.mapper;

import com.acco.life.dto.UserGroupMemberDto;
import com.acco.life.entity.UserGroupMember;
import org.mapstruct.Mapper;

/**
 * description: 用户组成员映射器，负责DTO与实体的转换
 *
 * @date: 2025-07-24 17:54:23
 * @author wensen.zhang
 * @version V1.0.0
 */
@Mapper(componentModel = "spring")
public interface UserGroupMemberMapper {

    UserGroupMember toEntity(UserGroupMemberDto dto);

    UserGroupMemberDto toDto(UserGroupMember entity);

}
