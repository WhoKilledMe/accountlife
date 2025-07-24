package com.acco.life.service.impl;

import com.acco.life.dto.FileTransactionDto;
import com.acco.life.enums.TransactionSourceType;
import com.acco.life.factory.FileTransactionFactory;
import com.acco.life.mapper.AccountTransactionMapper;
import com.acco.life.service.TransactionCsvParseService;
import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * description: [此处简要描述文件功能]
 *
 * @author wensen.zhang
 * @version V1.0.0
 * @date: 2025-07-22 20:16:08
 */
@Service
@RequiredArgsConstructor
public class TransactionCsvParseServiceImpl implements TransactionCsvParseService {
    private final CsvMapper csvMapper = new CsvMapper();

    private final AccountTransactionMapper mapper;
    @Override
    public void parseCsvFile(InputStream inputStream, TransactionSourceType transactionSourceType) {
        Class<? extends FileTransactionDto> targetType = FileTransactionFactory.fileTransactionMap.get(transactionSourceType);
        if (targetType == null) {
            throw new IllegalArgumentException("不支持的 type: " + transactionSourceType);
        }

        CsvSchema schema = csvMapper.schemaFor(targetType).withHeader();
        try {
            MappingIterator<? extends FileTransactionDto> it = csvMapper.readerFor(targetType)
                    .with(schema)
                    .readValues(inputStream);
            List<? extends FileTransactionDto> fileTransactionDtos = it.readAll();
            mapper.toEntityList(fileTransactionDtos, transactionSourceType);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }


    }
}
