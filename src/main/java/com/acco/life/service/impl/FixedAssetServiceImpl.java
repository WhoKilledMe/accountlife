package com.acco.life.service.impl;

import com.acco.life.dto.FixedAssetDto;
import com.acco.life.mapper.FixedAssetMapper;
import com.acco.life.repository.FixedAssetRepository;
import com.acco.life.service.FixedAssetService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FixedAssetServiceImpl implements FixedAssetService {

    private final FixedAssetRepository repository;

    private final FixedAssetMapper mapper;


    @Override
    public Mono<List<FixedAssetDto>> findAll() {
        return repository.findAll()
                .map(mapper::toDto).collectList();
    }

    @Override
    public Mono<FixedAssetDto> findById(Integer id) {
        return repository.findById(id)
        .map(mapper::toDto);
    }

    @Override
    public Mono<FixedAssetDto> save(Mono<FixedAssetDto> dto) {

        return dto.map(mapper::toEntity)
            .flatMap(repository::save)
            .map(mapper::toDto);
    }

    @Override
    public Mono<Void> deleteById(Integer id) {
        return repository.deleteById(id);
    }
}
