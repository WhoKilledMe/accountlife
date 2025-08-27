package com.acco.life.service;

import com.acco.life.dto.AccountTransactionDto;
import com.acco.life.common.PageResponse;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * description: 账户交易服务接口，定义账户交易的增删改查操作
 *
 * @date: 2025-07-24 17:54:23
 * @author wensen.zhang
 * @version V1.0.0
 */
public interface AccountTransactionService {

     Mono<List<AccountTransactionDto>> findAll();

     Mono<AccountTransactionDto> findById(Integer id);

     Mono<AccountTransactionDto> save(Mono<AccountTransactionDto> entity);

     Mono<Void> deleteById(Integer id);

     /**
      * 数据库分页+模糊搜索
      */
     Mono<PageResponse<AccountTransactionDto>> page(AccountTransactionDto filter, int page, int size);
}