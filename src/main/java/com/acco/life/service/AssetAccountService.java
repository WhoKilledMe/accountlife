package com.acco.life.service;

import com.acco.life.dto.AssetAccountDto;
import com.acco.life.common.PageResponse;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * description: 资产账户服务接口，定义资产账户的增删改查操作
 *
 * @date: 2025-07-24 17:54:23
 * @author wensen.zhang
 * @version V1.0.0
 */
public interface AssetAccountService {

     Mono<List<AssetAccountDto>> findAll();

     Mono<AssetAccountDto> findById(Long id);

     Mono<AssetAccountDto> save(Mono<AssetAccountDto> entity);

     Mono<Void> deleteById(Long id);

     /**
      * 数据库分页+模糊搜索
      */
     Mono<PageResponse<AssetAccountDto>> page(AssetAccountDto filter, int page, int size);
}