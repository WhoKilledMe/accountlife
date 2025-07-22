package com.acco.life.mapper;

import com.acco.life.dto.FixedAssetDto;
import com.acco.life.entity.FixedAsset;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface FixedAssetMapper {

    FixedAsset toEntity(FixedAssetDto dto);

    FixedAssetDto toDto(FixedAsset entity);

}
