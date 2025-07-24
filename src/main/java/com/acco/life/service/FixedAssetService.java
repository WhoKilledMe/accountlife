package com.acco.life.service;

import com.acco.life.dto.FixedAssetDto;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * description: 固定资产服务接口，定义固定资产的增删改查操作
 *
 * @date: 2025-07-24 17:54:23
 * @author wensen.zhang
 * @version V1.0.0
 */
public interface FixedAssetService {

     Mono<List<FixedAssetDto>> findAll();

     Mono<FixedAssetDto> findById(Integer id);

     Mono<FixedAssetDto> save(Mono<FixedAssetDto> entity);

     Mono<Void> deleteById(Integer id);
}