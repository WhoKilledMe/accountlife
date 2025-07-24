package com.acco.life.service;

import com.acco.life.dto.CreditWalletStatementDto;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * description: 信用钱包账单服务接口，定义信用钱包账单的增删改查操作
 *
 * @date: 2025-07-24 17:54:23
 * @author wensen.zhang
 * @version V1.0.0
 */
public interface CreditWalletStatementService {

     Mono<List<CreditWalletStatementDto>> findAll();

     Mono<CreditWalletStatementDto> findById(Integer id);

     Mono<CreditWalletStatementDto> save(Mono<CreditWalletStatementDto> entity);

     Mono<Void> deleteById(Integer id);
}