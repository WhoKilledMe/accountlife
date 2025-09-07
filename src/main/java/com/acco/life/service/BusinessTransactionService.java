package com.acco.life.service;

import com.acco.life.dto.BusinessTransactionDto;
import com.acco.life.common.PageResponse;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * description: 业务交易服务接口，定义业务交易的增删改查操作
 *
 * @date: 2025-01-15 10:00:00
 * @author wensen.zhang
 * @version V1.0.0
 */
public interface BusinessTransactionService {

     Mono<List<BusinessTransactionDto>> findAll();

     Mono<BusinessTransactionDto> findById(Long id);

     Mono<BusinessTransactionDto> save(Mono<BusinessTransactionDto> entity);

     Mono<Void> deleteById(Long id);

     /**
      * 数据库分页+模糊搜索
      */
     Mono<PageResponse<BusinessTransactionDto>> page(BusinessTransactionDto filter, int page, int size);
}
