package com.acco.life.service;

import com.acco.life.dto.InvestmentAssetDto;
import reactor.core.publisher.Mono;

import java.util.List;

public interface InvestmentAssetService {

     Mono<List<InvestmentAssetDto>> findAll();

     Mono<InvestmentAssetDto> findById(Integer id);

     Mono<InvestmentAssetDto> save(Mono<InvestmentAssetDto> entity);

     Mono<Void> deleteById(Integer id);
}