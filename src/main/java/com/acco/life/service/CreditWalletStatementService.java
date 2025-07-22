package com.acco.life.service;

import com.acco.life.dto.CreditWalletStatementDto;
import reactor.core.publisher.Mono;

import java.util.List;

public interface CreditWalletStatementService {

     Mono<List<CreditWalletStatementDto>> findAll();

     Mono<CreditWalletStatementDto> findById(Integer id);

     Mono<CreditWalletStatementDto> save(Mono<CreditWalletStatementDto> entity);

     Mono<Void> deleteById(Integer id);
}