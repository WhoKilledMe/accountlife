package com.acco.life.service;

import com.acco.life.common.PageResponse;
import com.acco.life.dto.UserMailConfigDto;
import reactor.core.publisher.Mono;

import java.util.List;

public interface UserMailConfigService {
    Mono<List<UserMailConfigDto>> findAll();
    Mono<UserMailConfigDto> findById(Long id);
    Mono<UserMailConfigDto> save(Mono<UserMailConfigDto> dto);
    Mono<Void> deleteById(Long id);
    Mono<PageResponse<UserMailConfigDto>> page(UserMailConfigDto filter, int page, int size);
    Mono<List<UserMailConfigDto>> query(UserMailConfigDto filter);
    Mono<UserMailConfigDto> findOneByUserId(Long userId);

    /**
     * 测试邮箱配置连通性（基础网络连通性）
     */
    Mono<Boolean> testConnectivity(UserMailConfigDto config);
}


