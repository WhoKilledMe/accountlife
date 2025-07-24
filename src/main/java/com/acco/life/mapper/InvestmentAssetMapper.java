package com.acco.life.mapper;

import com.acco.life.dto.InvestmentAssetDto;
import com.acco.life.entity.InvestmentAsset;
import org.mapstruct.Mapper;

/**
 * description: 投资资产映射器，负责DTO与实体的转换
 *
 * @date: 2025-07-24 17:54:23
 * @author wensen.zhang
 * @version V1.0.0
 */
@Mapper(componentModel = "spring")
public interface InvestmentAssetMapper {

    InvestmentAsset toEntity(InvestmentAssetDto dto);

    InvestmentAssetDto toDto(InvestmentAsset entity);

}
