package com.acco.life.service.impl;

import com.acco.life.dto.CreditWalletStatementDto;
import com.acco.life.mapper.CreditWalletStatementMapper;
import com.acco.life.repository.CreditWalletStatementRepository;
import com.acco.life.service.CreditWalletStatementService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class CreditWalletStatementServiceImpl implements CreditWalletStatementService {

    private final CreditWalletStatementRepository repository;

    private final CreditWalletStatementMapper mapper;


    @Override
    public Flux<CreditWalletStatementDto> findAll() {
        return repository.findAll()
    .map(mapper::toDto);
    }

    @Override
    public Mono<CreditWalletStatementDto> findById(Integer id) {
        return repository.findById(id)
        .map(mapper::toDto);
    }

    @Override
    public Mono<CreditWalletStatementDto> save(Mono<CreditWalletStatementDto> dto) {

        return dto.map(mapper::toEntity)
            .flatMap(repository::save)
            .map(mapper::toDto);
    }

    @Override
    public Mono<Void> deleteById(Integer id) {
        return repository.deleteById(id);
    }
}
