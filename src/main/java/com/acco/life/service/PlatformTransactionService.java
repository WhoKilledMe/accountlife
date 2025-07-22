package com.acco.life.service;

import com.acco.life.dto.PlatformTransactionDto;
import reactor.core.publisher.Mono;

import java.util.List;

public interface PlatformTransactionService {

     Mono<List<PlatformTransactionDto>> findAll();

     Mono<PlatformTransactionDto> findById(Integer id);

     Mono<PlatformTransactionDto> save(Mono<PlatformTransactionDto> entity);

     Mono<Void> deleteById(Integer id);
}