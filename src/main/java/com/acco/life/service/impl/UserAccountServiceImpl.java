package com.acco.life.service.impl;

import com.acco.life.dto.UserAccountDto;
import com.acco.life.common.PageResponse;
import com.acco.life.mapper.UserAccountMapper;
import com.acco.life.repository.UserAccountRepository;
import com.acco.life.service.UserAccountService;
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
public class UserAccountServiceImpl implements UserAccountService {

    private final UserAccountRepository repository;
    

    private final UserAccountMapper mapper;


    @Override
    public Mono<List<UserAccountDto>> findAll() {
        return repository.findAll()
                .map(mapper::toDto).collectList();
    }

    @Override
    public Mono<UserAccountDto> findById(Long id) {
        return repository.findById(id)
        .map(mapper::toDto);
    }

    @Override
    public Mono<UserAccountDto> save(Mono<UserAccountDto> dto) {

        return dto.map(mapper::toEntity)
            .flatMap(repository::save)
            .map(mapper::toDto);
    }

    @Override
    public Mono<Void> deleteById(Long  id) {
        return repository.deleteById(id);
    }

    @Override
    public Mono<PageResponse<UserAccountDto>> page(UserAccountDto filter, int page, int size) {
        if (filter == null) {
            filter = new UserAccountDto();
        }
        final int currentPage = Math.max(page, 0);
        final int pageSize = Math.max(size, 1);
        long offset = (long) currentPage * pageSize;
        long limit = pageSize;

        String likeName = (filter.getName() == null || filter.getName().isEmpty()) ? null : "%" + filter.getName() + "%";
        String likePlatform = (filter.getPlatformCode() == null || filter.getPlatformCode().isEmpty()) ? null : "%" + filter.getPlatformCode() + "%";
        String likeAccount = (filter.getAccountNumber() == null || filter.getAccountNumber().isEmpty()) ? null : "%" + filter.getAccountNumber() + "%";
        Long  userId = filter.getUserId();

        Mono<java.util.List<UserAccountDto>> dataMono = repository.search(likeName, likePlatform, likeAccount, userId, limit, offset)
                .map(mapper::toDto)
                .collectList();

        Mono<Long> countMono = repository.countSearch(likeName, likePlatform, likeAccount, userId);

        return reactor.core.publisher.Mono.zip(dataMono, countMono)
                .map(tuple -> PageResponse.of(tuple.getT1(), currentPage, pageSize, tuple.getT2()));
    }
}
