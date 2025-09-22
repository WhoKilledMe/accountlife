package com.acco.life.service.impl;

import com.acco.life.dto.AccountConfigDto;
import com.acco.life.mapper.AccountConfigMapper;
import com.acco.life.repository.AccountConfigRepository;
import com.acco.life.service.AccountConfigService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import com.acco.life.common.PageResponse;
import com.acco.life.entity.AccountConfig;
import com.acco.life.repository.UserAccountRepository;
import com.acco.life.util.UserUtil;

/**
 * 账户配置服务实现类
 *
 * @author wensen.zhang
 * @version V1.0.0
 */
@Service
@RequiredArgsConstructor
public class AccountConfigServiceImpl implements AccountConfigService {
    
    private final AccountConfigRepository repository;
    private final AccountConfigMapper mapper;
    private final UserAccountRepository userAccountRepository;
    
    @Override
    public Flux<AccountConfigDto> findAllActive() {
        return repository.findAllActiveOrderByTypeAndSortOrder()
                .map(mapper::toDto);
    }
    
    @Override
    public Flux<AccountConfigDto> findByType(Integer type) {
        return repository.findByTypeAndActiveOrderBySortOrder(type)
                .map(mapper::toDto);
    }
    
    @Override
    public Flux<AccountConfigDto> findByNameLike(String nameLike) {
        String likePattern = (nameLike == null || nameLike.isEmpty()) ? null : "%" + nameLike + "%";
        return repository.findActiveByNameLikeOrderByTypeAndSortOrder(likePattern)
                .map(mapper::toDto);
    }
    
    @Override
    public Mono<List<AccountConfigDto>> getAllForSelect() {
        return findAllActive().collectList();
    }

    @Override
    public Mono<PageResponse<AccountConfigDto>> page(AccountConfigDto filter, int page, int size) {
        final int currentPage = Math.max(page, 0);
        final int pageSize = Math.max(size, 1);
        long offset = (long) currentPage * pageSize;
        long limit = pageSize;

        String likeName = (filter == null || filter.getName() == null || filter.getName().isEmpty()) ? null : "%" + filter.getName() + "%";
        Integer type = (filter == null) ? null : filter.getType();
        String likePlatform = (filter == null || filter.getPlatformCode() == null || filter.getPlatformCode().isEmpty()) ? null : "%" + filter.getPlatformCode() + "%";
        Boolean active = (filter == null) ? null : filter.getIsActive();

        Mono<List<AccountConfigDto>> dataMono = repository.search(likeName, type, likePlatform, active, limit, offset)
                .map(mapper::toDto)
                .collectList()
                .flatMap(list -> UserUtil.getCurrentUserId()
                        .flatMap(uid -> userAccountRepository.findAll()
                                .filter(a -> a.getUserId() != null && a.getPlatformCode() != null && a.getUserId().equals(uid))
                                .map(a -> a.getPlatformCode())
                                .collectList()
                                .map(userCodes -> {
                                    list.forEach(dto -> dto.setIsUsed(userCodes.contains(dto.getPlatformCode())));
                                    return list;
                                })
                        )
                        .defaultIfEmpty(list)
                );
        Mono<Long> countMono = repository.countSearch(likeName, type, likePlatform, active);
        return Mono.zip(dataMono, countMono)
                .map(tuple -> PageResponse.of(tuple.getT1(), currentPage, pageSize, tuple.getT2()));
    }

    @Override
    public Mono<AccountConfigDto> findById(Long id) {
        return repository.findById(id).map(mapper::toDto);
    }

    @Override
    public Mono<AccountConfigDto> save(Mono<AccountConfigDto> dtoMono) {
        return dtoMono
                .map(mapper::toEntity)
                .flatMap(this::ensureDefaults)
                .flatMap(repository::save)
                .map(mapper::toDto);
    }

    private Mono<AccountConfig> ensureDefaults(AccountConfig entity) {
        if (entity.getIsDeleted() == null) {
            entity.setIsDeleted(0);
        }
        if (entity.getIsActive() == null) {
            entity.setIsActive(Boolean.TRUE);
        }
        return Mono.just(entity);
    }

    @Override
    public Mono<Void> deleteById(Long id) {
        return repository.findById(id)
                .flatMap(e -> {
                    e.setIsDeleted(1);
                    return repository.save(e);
                })
                .then();
    }
}
