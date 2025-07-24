package com.acco.life.service;

import com.acco.life.dto.InvestmentAssetDto;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * description: 投资资产服务接口，定义投资资产的增删改查操作
 *
 * @date: 2025-07-24 17:54:23
 * @author wensen.zhang
 * @version V1.0.0
 */
public interface InvestmentAssetService {

     Mono<List<InvestmentAssetDto>> findAll();

     Mono<InvestmentAssetDto> findById(Integer id);

     Mono<InvestmentAssetDto> save(Mono<InvestmentAssetDto> entity);

     Mono<Void> deleteById(Integer id);
}