package com.acco.life.mapper;

import com.acco.life.dto.InvestmentAssetDto;
import com.acco.life.entity.InvestmentAsset;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface InvestmentAssetMapper {

    InvestmentAsset toEntity(InvestmentAssetDto dto);

    InvestmentAssetDto toDto(InvestmentAsset entity);

}
