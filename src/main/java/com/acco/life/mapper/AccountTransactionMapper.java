package com.acco.life.mapper;

import com.acco.life.dto.AccountTransactionDto;
import com.acco.life.dto.FileTransactionDto;
import com.acco.life.dto.FileTransactionNingBoBank;
import com.acco.life.entity.AccountTransaction;
import com.acco.life.enums.TransactionSourceType;
import org.mapstruct.Mapper;

import java.util.ArrayList;
import java.util.List;

@Mapper(componentModel = "spring")
public interface AccountTransactionMapper {

    AccountTransaction toEntity(AccountTransactionDto dto);

    AccountTransactionDto toDto(AccountTransaction entity);

    default List<AccountTransaction> toEntityList(List<? extends FileTransactionDto> dto, TransactionSourceType transactionSourceType) {
        switch (transactionSourceType) {
            case NING_BO_CREDIT -> {
                for (FileTransactionDto fileTransactionDto : dto) {
                    if(fileTransactionDto instanceof  FileTransactionNingBoBank ningbo) {

                    }
                }
            }
        }

        return new ArrayList<>();
    }

}
