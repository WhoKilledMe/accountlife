package com.acco.life.mapper;

import com.acco.life.dto.UserGroupMemberDto;
import com.acco.life.entity.UserGroupMember;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserGroupMemberMapper {

    UserGroupMember toEntity(UserGroupMemberDto dto);

    UserGroupMemberDto toDto(UserGroupMember entity);

}
