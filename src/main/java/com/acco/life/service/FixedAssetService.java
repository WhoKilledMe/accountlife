package com.acco.life.service;

import com.acco.life.dto.FixedAssetDto;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface FixedAssetService {

     Flux<FixedAssetDto> findAll();

     Mono<FixedAssetDto> findById(Integer id);

     Mono<FixedAssetDto> save(Mono<FixedAssetDto> entity);

     Mono<Void> deleteById(Integer id);
}