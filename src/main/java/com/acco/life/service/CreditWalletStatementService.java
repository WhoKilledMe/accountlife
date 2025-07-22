package com.acco.life.service;

import com.acco.life.dto.CreditWalletStatementDto;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface CreditWalletStatementService {

     Flux<CreditWalletStatementDto> findAll();

     Mono<CreditWalletStatementDto> findById(Integer id);

     Mono<CreditWalletStatementDto> save(Mono<CreditWalletStatementDto> entity);

     Mono<Void> deleteById(Integer id);
}