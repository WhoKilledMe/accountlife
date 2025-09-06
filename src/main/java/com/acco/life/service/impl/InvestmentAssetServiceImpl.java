package com.acco.life.service.impl;

import com.acco.life.dto.InvestmentAssetDto;
import com.acco.life.mapper.InvestmentAssetMapper;
import com.acco.life.repository.InvestmentAssetRepository;
import com.acco.life.service.InvestmentAssetService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * description: 投资资产服务实现类，实现投资资产的增删改查业务逻辑
 *
 * @date: 2025-07-24 17:54:23
 * @author wensen.zhang
 * @version V1.0.0
 */

@Service
@RequiredArgsConstructor
public class InvestmentAssetServiceImpl implements InvestmentAssetService {

    private final InvestmentAssetRepository repository;

    private final InvestmentAssetMapper mapper;


    @Override
    public Mono<List<InvestmentAssetDto>> findAll() {
        return repository.findAll()
                .map(mapper::toDto).collectList();
    }

    @Override
    public Mono<InvestmentAssetDto> findById(Long id) {
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
    public Mono<Void> deleteById(Long id) {
        return repository.deleteById(id);
    }
}
