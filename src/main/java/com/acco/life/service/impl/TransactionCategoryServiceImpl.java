package com.acco.life.service.impl;

import com.acco.life.dto.TransactionCategoryDto;
import com.acco.life.mapper.TransactionCategoryMapper;
import com.acco.life.repository.TransactionCategoryRepository;
import com.acco.life.service.TransactionCategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class TransactionCategoryServiceImpl implements TransactionCategoryService {

    private final TransactionCategoryRepository repository;

    private final TransactionCategoryMapper mapper;


    @Override
    public Flux<TransactionCategoryDto> findAll() {
        return repository.findAll()
    .map(mapper::toDto);
    }

    @Override
    public Mono<TransactionCategoryDto> findById(Integer id) {
        return repository.findById(id)
        .map(mapper::toDto);
    }

    @Override
    public Mono<TransactionCategoryDto> save(Mono<TransactionCategoryDto> dto) {

        return dto.map(mapper::toEntity)
            .flatMap(repository::save)
            .map(mapper::toDto);
    }

    @Override
    public Mono<Void> deleteById(Integer id) {
        return repository.deleteById(id);
    }
}
