package com.acco.life.service;

import com.acco.life.dto.AccountTransactionDto;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface AccountTransactionService {

     Flux<AccountTransactionDto> findAll();

     Mono<AccountTransactionDto> findById(Integer id);

     Mono<AccountTransactionDto> save(Mono<AccountTransactionDto> entity);

     Mono<Void> deleteById(Integer id);
}