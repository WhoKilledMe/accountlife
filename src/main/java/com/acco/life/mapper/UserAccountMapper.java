package com.acco.life.mapper;

import com.acco.life.dto.UserAccountDto;
import com.acco.life.entity.UserAccount;
import org.mapstruct.Mapper;

/**
 * description: 资产账户映射器，负责DTO与实体的转换
 *
 * @date: 2025-07-24 17:54:23
 * @author wensen.zhang
 * @version V1.0.0
 */
@Mapper(componentModel = "spring")
public interface UserAccountMapper {

    UserAccount toEntity(UserAccountDto dto);

    UserAccountDto toDto(UserAccount entity);

}
