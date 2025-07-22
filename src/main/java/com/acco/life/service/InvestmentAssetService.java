package com.acco.life.service;

import com.acco.life.dto.InvestmentAssetDto;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface InvestmentAssetService {

     Flux<InvestmentAssetDto> findAll();

     Mono<InvestmentAssetDto> findById(Integer id);

     Mono<InvestmentAssetDto> save(Mono<InvestmentAssetDto> entity);

     Mono<Void> deleteById(Integer id);
}