package com.acco.life.service;

import com.acco.life.dto.FixedAssetDto;
import reactor.core.publisher.Mono;

import java.util.List;

public interface FixedAssetService {

     Mono<List<FixedAssetDto>> findAll();

     Mono<FixedAssetDto> findById(Integer id);

     Mono<FixedAssetDto> save(Mono<FixedAssetDto> entity);

     Mono<Void> deleteById(Integer id);
}