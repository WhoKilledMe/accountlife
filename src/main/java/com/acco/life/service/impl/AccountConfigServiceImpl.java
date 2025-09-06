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
}
