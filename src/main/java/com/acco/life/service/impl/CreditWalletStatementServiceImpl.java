package com.acco.life.service.impl;

import com.acco.life.dto.CreditWalletStatementDto;
import com.acco.life.mapper.CreditWalletStatementMapper;
import com.acco.life.repository.CreditWalletStatementRepository;
import com.acco.life.service.CreditWalletStatementService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * description: 信用钱包账单服务实现类，实现信用钱包账单的增删改查业务逻辑
 *
 * @date: 2025-07-24 17:54:23
 * @author wensen.zhang
 * @version V1.0.0
 */

@Service
@RequiredArgsConstructor
public class CreditWalletStatementServiceImpl implements CreditWalletStatementService {

    private final CreditWalletStatementRepository repository;

    private final CreditWalletStatementMapper mapper;


    @Override
    public Mono<List<CreditWalletStatementDto>> findAll() {
        return repository.findAll()
                .map(mapper::toDto).collectList();
    }

    @Override
    public Mono<CreditWalletStatementDto> findById(Long id) {
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
    public Mono<Void> deleteById(Long id) {
        return repository.deleteById(id);
    }
}
