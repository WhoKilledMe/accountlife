package com.acco.life.mapper;

import com.acco.life.dto.CreditWalletStatementDto;
import com.acco.life.entity.CreditWalletStatement;
import org.mapstruct.Mapper;

/**
 * description: 信用钱包账单映射器，负责DTO与实体的转换
 *
 * @date: 2025-07-24 17:54:23
 * @author wensen.zhang
 * @version V1.0.0
 */
@Mapper(componentModel = "spring")
public interface CreditWalletStatementMapper {

    CreditWalletStatement toEntity(CreditWalletStatementDto dto);

    CreditWalletStatementDto toDto(CreditWalletStatement entity);

}
