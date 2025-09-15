package com.acco.life.service.csv;

import com.acco.life.dto.FileTransactionDto;
import com.acco.life.enums.TransactionSourceType;
import com.acco.life.entity.AccountTransaction;
import com.acco.life.mapper.AccountTransactionMapper;
import com.acco.life.service.AiTransactionCategoryService;

import java.io.InputStream;
import java.util.List;
import reactor.core.publisher.Mono;

/**
 * CSV解析策略接口
 */
public interface CsvParserStrategy {

    /**
     * 是否支持该来源类型
     */
    boolean supports(TransactionSourceType sourceType);

    /**
     * 将CSV输入流解析为文件交易DTO列表
     */
    List<? extends FileTransactionDto> parse(InputStream inputStream) throws Exception;

    /**
     * 将解析得到的 DTO 列表转换为交易实体的 Mono 列表
     */
    List<Mono<AccountTransaction>> buildTransactions(List<? extends FileTransactionDto> dtos,
                                                     Long userId,
                                                     Long accountId,
                                                     TransactionSourceType sourceType,
                                                     AccountTransactionMapper mapper,
                                                     AiTransactionCategoryService aiService);
}


