package com.acco.life.mapper;

import com.acco.life.dto.AccountTransactionUploadFileLogDto;
import com.acco.life.entity.AccountTransactionUploadFileLog;
import org.mapstruct.Mapper;

/**
 * description: 交易导入文件日志 Mapper
 *
 * @author wensen.zhang
 */
@Mapper(componentModel = "spring")
public interface AccountTransactionUploadFileLogMapper {

    AccountTransactionUploadFileLog toEntity(AccountTransactionUploadFileLogDto dto);

    AccountTransactionUploadFileLogDto toDto(AccountTransactionUploadFileLog entity);
}


