package com.acco.life.mapper;

import com.acco.life.dto.AccountConfigDto;
import com.acco.life.entity.AccountConfig;
import org.mapstruct.Mapper;

/**
 * 账户配置映射器
 *
 * @author wensen.zhang
 * @version V1.0.0
 */
@Mapper(componentModel = "spring")
public interface AccountConfigMapper {

    AccountConfigDto toDto(AccountConfig entity);
    
    AccountConfig toEntity(AccountConfigDto dto);
}
