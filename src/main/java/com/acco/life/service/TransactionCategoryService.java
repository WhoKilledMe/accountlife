package com.acco.life.service;

import com.acco.life.dto.TransactionCategoryDto;
import reactor.core.publisher.Mono;

import java.util.List;

public interface TransactionCategoryService {

     Mono<List<TransactionCategoryDto>> findAll();

     Mono<TransactionCategoryDto> findById(Integer id);

     Mono<TransactionCategoryDto> save(Mono<TransactionCategoryDto> entity);

     Mono<Void> deleteById(Integer id);
}