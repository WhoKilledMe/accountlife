package com.acco.life.service.impl;

import com.acco.life.dto.AssetAccountDto;
import com.acco.life.mapper.AssetAccountMapper;
import com.acco.life.repository.AssetAccountRepository;
import com.acco.life.service.AssetAccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class AssetAccountServiceImpl implements AssetAccountService {

    private final AssetAccountRepository repository;

    private final AssetAccountMapper mapper;


    @Override
    public Flux<AssetAccountDto> findAll() {
        return repository.findAll()
    .map(mapper::toDto);
    }

    @Override
    public Mono<AssetAccountDto> findById(Integer id) {
        return repository.findById(id)
        .map(mapper::toDto);
    }

    @Override
    public Mono<AssetAccountDto> save(Mono<AssetAccountDto> dto) {

        return dto.map(mapper::toEntity)
            .flatMap(repository::save)
            .map(mapper::toDto);
    }

    @Override
    public Mono<Void> deleteById(Integer id) {
        return repository.deleteById(id);
    }
}
