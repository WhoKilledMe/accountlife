package com.acco.life.service;

import com.acco.life.dto.PlatformTransactionDto;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface PlatformTransactionService {

     Flux<PlatformTransactionDto> findAll();

     Mono<PlatformTransactionDto> findById(Integer id);

     Mono<PlatformTransactionDto> save(Mono<PlatformTransactionDto> entity);

     Mono<Void> deleteById(Integer id);
}