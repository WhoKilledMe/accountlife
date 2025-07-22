package com.acco.life.service;

import com.acco.life.dto.AccountTransactionDto;
import reactor.core.publisher.Mono;

import java.util.List;

public interface AccountTransactionService {

     Mono<List<AccountTransactionDto>> findAll();

     Mono<AccountTransactionDto> findById(Integer id);

     Mono<AccountTransactionDto> save(Mono<AccountTransactionDto> entity);

     Mono<Void> deleteById(Integer id);
}