package com.acco.life.service.impl;

import com.acco.life.dto.AssetAccountDto;
import com.acco.life.common.PageResponse;
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

    @Override
    public Mono<PageResponse<AssetAccountDto>> page(AssetAccountDto filter, int page, int size) {
        if (filter == null) {
            filter = new AssetAccountDto();
        }
        final int currentPage = Math.max(page, 0);
        final int pageSize = Math.max(size, 1);
        long offset = (long) currentPage * pageSize;
        long limit = pageSize;

        String likeName = (filter.getName() == null || filter.getName().isEmpty()) ? null : "%" + filter.getName() + "%";
        String likePlatform = (filter.getPlatformCode() == null || filter.getPlatformCode().isEmpty()) ? null : "%" + filter.getPlatformCode() + "%";
        String likeAccount = (filter.getAccountNumber() == null || filter.getAccountNumber().isEmpty()) ? null : "%" + filter.getAccountNumber() + "%";
        Integer userId = filter.getUserId();

        Mono<java.util.List<AssetAccountDto>> dataMono = repository.search(likeName, likePlatform, likeAccount, userId, limit, offset)
                .map(mapper::toDto)
                .collectList();

        Mono<Long> countMono = repository.countSearch(likeName, likePlatform, likeAccount, userId);

        return reactor.core.publisher.Mono.zip(dataMono, countMono)
                .map(tuple -> PageResponse.of(tuple.getT1(), currentPage, pageSize, tuple.getT2()));
    }
}
