package com.acco.life.service;

import com.acco.life.dto.TransactionCategoryDto;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface TransactionCategoryService {

     Flux<TransactionCategoryDto> findAll();

     Mono<TransactionCategoryDto> findById(Integer id);

     Mono<TransactionCategoryDto> save(Mono<TransactionCategoryDto> entity);

     Mono<Void> deleteById(Integer id);
}