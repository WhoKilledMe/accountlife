package com.acco.life.service;

import com.acco.life.dto.AssetAccountDto;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface AssetAccountService {

     Flux<AssetAccountDto> findAll();

     Mono<AssetAccountDto> findById(Integer id);

     Mono<AssetAccountDto> save(Mono<AssetAccountDto> entity);

     Mono<Void> deleteById(Integer id);
}