package com.acco.life.service;

import com.acco.life.dto.PlatformTransactionDto;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * description: 平台交易服务接口，定义平台交易的增删改查操作
 *
 * @date: 2025-07-24 17:54:23
 * @author wensen.zhang
 * @version V1.0.0
 */
public interface PlatformTransactionService {

     Mono<List<PlatformTransactionDto>> findAll();

     Mono<PlatformTransactionDto> findById(Long id);

     Mono<PlatformTransactionDto> save(Mono<PlatformTransactionDto> entity);

     Mono<Void> deleteById(Long id);
}