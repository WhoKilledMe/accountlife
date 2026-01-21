package com.acco.life.service.csv;

import com.acco.life.dto.FileTransactionDto;
import com.acco.life.dto.FileTransactionLabelDetail;
import com.acco.life.dto.FileTransactionMeituan;
import com.acco.life.enums.TransactionSourceType;
import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

@Component
public class MeituanCsvParser implements CsvParserStrategy {

    private final CsvMapper csvMapper = new CsvMapper();

    @Override
    public boolean supports(TransactionSourceType sourceType) {
        return sourceType == TransactionSourceType.PLATFORM;
    }

    @Override
    public List<? extends FileTransactionDto> parse(InputStream inputStream) throws Exception {
        CsvSchema schema = csvMapper.schemaFor(FileTransactionMeituan.class).withHeader();
        MappingIterator<FileTransactionMeituan> it = csvMapper.readerFor(FileTransactionMeituan.class)
                .with(schema)
                .readValues(inputStream);
        List<FileTransactionMeituan> list = it.readAll();
        List<FileTransactionLabelDetail> normalized = new ArrayList<>();
        for (FileTransactionMeituan mt : list) {
            if (mt.getOrderTitle() == null || mt.getOrderTitle().isEmpty()) {
                continue;
            }
            if (mt.getPaidAmount() == null || mt.getPaidAmount().isEmpty()) {
                continue;
            }
            FileTransactionLabelDetail tmp = getFileTransactionLabelDetail(mt);
            normalized.add(tmp);
        }
        return normalized;
    }

    private static FileTransactionLabelDetail getFileTransactionLabelDetail(FileTransactionMeituan mt) {
        FileTransactionLabelDetail tmp = new FileTransactionLabelDetail();
        if (mt.getSuccessTime() != null && mt.getSuccessTime().length() >= 10) {
            tmp.setTransactionDate(mt.getSuccessTime().substring(0, 10));
            tmp.setAccountingDate(tmp.getTransactionDate());
        }
        tmp.setTransactionSummary(mt.getOrderTitle());
        String amt = mt.getPaidAmount().replace("¥", "").trim();
        if ("收入".equals(mt.getIncomeOrExpense())) {
            tmp.setTransactionAmount(amt);
        } else {
            tmp.setTransactionAmount("-" + amt);
        }
        return tmp;
    }

    @Override
    public List<Mono<com.acco.life.entity.AccountTransaction>> buildTransactions(List<? extends FileTransactionDto> dtos,
                                                                                 Long userId,
                                                                                 Long accountId,
                                                                                 TransactionSourceType sourceType,
                                                                                 com.acco.life.mapper.AccountTransactionMapper mapper,
                                                                                 com.acco.life.service.AiTransactionCategoryService aiService) {
        List<Mono<com.acco.life.entity.AccountTransaction>> list = new ArrayList<>();
        for (FileTransactionDto dto : dtos) {
            FileTransactionLabelDetail ld = (FileTransactionLabelDetail) dto;
            Mono<com.acco.life.entity.AccountTransaction> mono = aiService
                    .inferTransactionCategory(ld.getTransactionSummary(), ld.getTransactionAmount(), userId)
                    .flatMap(category -> {
                        com.acco.life.entity.AccountTransaction tx = mapper.toEntity(ld, userId, accountId, sourceType.code);
                        if (category != null) {
                            tx.setCategoryId(category.getId());
                        }
                        return Mono.just(tx);
                    })
                    .switchIfEmpty(Mono.defer(() -> {
                        com.acco.life.entity.AccountTransaction tx = mapper.toEntity(ld, userId, accountId, sourceType.code);
                        return Mono.just(tx);
                    }));
            list.add(mono);
        }
        return list;
    }
}


