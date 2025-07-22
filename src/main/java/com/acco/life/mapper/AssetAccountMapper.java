package com.acco.life.mapper;

import com.acco.life.dto.AssetAccountDto;
import com.acco.life.entity.AssetAccount;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AssetAccountMapper {

    AssetAccount toEntity(AssetAccountDto dto);

    AssetAccountDto toDto(AssetAccount entity);

}
