package com.acco.life.service.impl;

import com.acco.life.dto.FileTransactionDto;
import com.acco.life.enums.TransactionSourceType;
import com.acco.life.factory.FileTransactionFactory;
import com.acco.life.mapper.AccountTransactionMapper;
import com.acco.life.repository.AccountTransactionRepository;
import com.acco.life.repository.AssetAccountRepository;
import com.acco.life.service.TransactionCsvParseService;
import com.acco.life.util.UserUtil;
import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * description: 交易CSV解析服务实现类，实现CSV文件解析与入库逻辑
 *
 * @date: 2025-07-24 17:54:23
 * @author wensen.zhang
 * @version V1.0.0
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class TransactionCsvParseServiceImpl implements TransactionCsvParseService {
    private final CsvMapper csvMapper = new CsvMapper();

    private final AccountTransactionMapper mapper;
    private final AccountTransactionRepository transactionRepository;
    private final AssetAccountRepository accountRepository;

    @Override
    public void parseCsvFile(InputStream inputStream, TransactionSourceType transactionSourceType, String accountName) {
        Class<? extends FileTransactionDto> targetType = FileTransactionFactory.fileTransactionMap.get(transactionSourceType);
        if (targetType == null) {
            throw new IllegalArgumentException("不支持的 type: " + transactionSourceType);
        }

        // 获取当前用户ID
        Integer userId = UserUtil.getCurrentUser().block().getId();
        
        // 根据用户ID和账户名称获取账户ID
        Integer accountId = accountRepository.findByUserIdAndName(userId, accountName).map(account -> {
            return account.getId();
        }).block();
        
        if (accountId == null) {
            throw new IllegalArgumentException("未找到账户名称为: " + accountName + " 的账户");
        }

        CsvSchema schema = csvMapper.schemaFor(targetType).withHeader();
        try {
            MappingIterator<? extends FileTransactionDto> it = csvMapper.readerFor(targetType)
                    .with(schema)
                    .readValues(inputStream);
            List<? extends FileTransactionDto> fileTransactionDtos = it.readAll();
            
            // 转换为AccountTransaction实体列表
            List<com.acco.life.entity.AccountTransaction> accountTransactions = 
                mapper.toEntityList(fileTransactionDtos, transactionSourceType, userId, accountId);
            
            // 保存到数据库
            transactionRepository.saveAll(accountTransactions).subscribe();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}