package com.acco.life.service;

import com.acco.life.dto.AssetAccountDto;
import reactor.core.publisher.Mono;

import java.util.List;

public interface AssetAccountService {

     Mono<List<AssetAccountDto>> findAll();

     Mono<AssetAccountDto> findById(Integer id);

     Mono<AssetAccountDto> save(Mono<AssetAccountDto> entity);

     Mono<Void> deleteById(Integer id);
}