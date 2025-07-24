package com.acco.life.service.impl;

import com.acco.life.dto.AssetAccountDto;
import com.acco.life.mapper.AssetAccountMapper;
import com.acco.life.repository.AssetAccountRepository;
import com.acco.life.service.AssetAccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * description: 资产账户服务实现类，实现资产账户的增删改查业务逻辑
 *
 * @date: 2025-07-24 17:54:23
 * @author wensen.zhang
 * @version V1.0.0
 */
@Service
@RequiredArgsConstructor
public class AssetAccountServiceImpl implements AssetAccountService {

    private final AssetAccountRepository repository;

    private final AssetAccountMapper mapper;


    @Override
    public Mono<List<AssetAccountDto>> findAll() {
        return repository.findAll()
                .map(mapper::toDto).collectList();
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
