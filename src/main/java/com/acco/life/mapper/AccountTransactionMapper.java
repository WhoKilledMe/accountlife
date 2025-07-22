package com.acco.life.mapper;

import com.acco.life.dto.AccountTransactionDto;
import com.acco.life.entity.AccountTransaction;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AccountTransactionMapper {

    AccountTransaction toEntity(AccountTransactionDto dto);

    AccountTransactionDto toDto(AccountTransaction entity);

}
