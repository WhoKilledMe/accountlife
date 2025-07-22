package com.acco.life.service.impl;

import com.acco.life.dto.InvestmentAssetDto;
import com.acco.life.mapper.InvestmentAssetMapper;
import com.acco.life.repository.InvestmentAssetRepository;
import com.acco.life.service.InvestmentAssetService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class InvestmentAssetServiceImpl implements InvestmentAssetService {

    private final InvestmentAssetRepository repository;

    private final InvestmentAssetMapper mapper;


    @Override
    public Flux<InvestmentAssetDto> findAll() {
        return repository.findAll()
    .map(mapper::toDto);
    }

    @Override
    public Mono<InvestmentAssetDto> findById(Integer id) {
        return repository.findById(id)
        .map(mapper::toDto);
    }

    @Override
    public Mono<InvestmentAssetDto> save(Mono<InvestmentAssetDto> dto) {

        return dto.map(mapper::toEntity)
            .flatMap(repository::save)
            .map(mapper::toDto);
    }

    @Override
    public Mono<Void> deleteById(Integer id) {
        return repository.deleteById(id);
    }
}
