package com.acco.life.service.impl;

import com.acco.life.dto.PlatformTransactionDto;
import com.acco.life.mapper.PlatformTransactionMapper;
import com.acco.life.repository.PlatformTransactionRepository;
import com.acco.life.service.PlatformTransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * description: 平台交易服务实现类，实现平台交易的增删改查业务逻辑
 *
 * @date: 2025-07-24 17:54:23
 * @author wensen.zhang
 * @version V1.0.0
 */

@Service
@RequiredArgsConstructor
public class PlatformTransactionServiceImpl implements PlatformTransactionService {

    private final PlatformTransactionRepository repository;

    private final PlatformTransactionMapper mapper;


    @Override
    public Mono<List<PlatformTransactionDto>> findAll() {
        return repository.findAll()
                .map(mapper::toDto).collectList();
    }

    @Override
    public Mono<PlatformTransactionDto> findById(Integer id) {
        return repository.findById(id)
        .map(mapper::toDto);
    }

    @Override
    public Mono<PlatformTransactionDto> save(Mono<PlatformTransactionDto> dto) {

        return dto.map(mapper::toEntity)
            .flatMap(repository::save)
            .map(mapper::toDto);
    }

    @Override
    public Mono<Void> deleteById(Integer id) {
        return repository.deleteById(id);
    }
}
