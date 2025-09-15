package com.acco.life.service.csv;

import com.acco.life.dto.FileTransactionDto;
import com.acco.life.dto.FileTransactionNingBoBank;
import com.acco.life.enums.TransactionSourceType;
import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.io.InputStream;
import java.util.List;

@Component
public class NingboCsvParser implements CsvParserStrategy {

    private final CsvMapper csvMapper = new CsvMapper();

    @Override
    public boolean supports(TransactionSourceType sourceType) {
        return sourceType == TransactionSourceType.NING_BO_CREDIT;
    }

    @Override
    public List<? extends FileTransactionDto> parse(InputStream inputStream) throws Exception {
        CsvSchema schema = csvMapper.schemaFor(FileTransactionNingBoBank.class).withHeader();
        MappingIterator<FileTransactionNingBoBank> it = csvMapper.readerFor(FileTransactionNingBoBank.class)
                .with(schema)
                .readValues(inputStream);
        return it.readAll();
    }

    @Override
    public List<Mono<com.acco.life.entity.AccountTransaction>> buildTransactions(List<? extends FileTransactionDto> dtos,
                                                                                 Long userId,
                                                                                 Long accountId,
                                                                                 TransactionSourceType sourceType,
                                                                                 com.acco.life.mapper.AccountTransactionMapper mapper,
                                                                                 com.acco.life.service.AiTransactionCategoryService aiService) {
        return dtos.stream().map(dto -> {
            FileTransactionNingBoBank nb = (FileTransactionNingBoBank) dto;
            return aiService.inferTransactionCategory(nb.getTransactionSummary(), nb.getTransactionAmount(), userId)
                    .flatMap(category -> {
                        com.acco.life.entity.AccountTransaction tx = mapper.toEntity(nb, userId, accountId, sourceType.code);
                        if (category != null) {
                            tx.setCategoryId(category.getId());
                        }
                        return Mono.just(tx);
                    })
                    .switchIfEmpty(Mono.defer(() -> Mono.just(mapper.toEntity(nb, userId, accountId, sourceType.code))));
        }).toList();
    }
}


