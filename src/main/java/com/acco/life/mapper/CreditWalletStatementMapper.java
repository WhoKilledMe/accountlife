package com.acco.life.mapper;

import com.acco.life.dto.CreditWalletStatementDto;
import com.acco.life.entity.CreditWalletStatement;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CreditWalletStatementMapper {

    CreditWalletStatement toEntity(CreditWalletStatementDto dto);

    CreditWalletStatementDto toDto(CreditWalletStatement entity);

}
