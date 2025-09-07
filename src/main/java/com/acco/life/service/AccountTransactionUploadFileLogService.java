package com.acco.life.service;

import com.acco.life.common.PageResponse;
import com.acco.life.dto.AccountTransactionUploadFileLogDto;
import reactor.core.publisher.Mono;

import java.util.List;

public interface AccountTransactionUploadFileLogService {

    Mono<List<AccountTransactionUploadFileLogDto>> findAll();

    Mono<AccountTransactionUploadFileLogDto> findById(Long id);

    Mono<AccountTransactionUploadFileLogDto> save(Mono<AccountTransactionUploadFileLogDto> dto);

    Mono<Void> deleteById(Long id);

    Mono<PageResponse<AccountTransactionUploadFileLogDto>> page(AccountTransactionUploadFileLogDto filter, int page, int size);
}


