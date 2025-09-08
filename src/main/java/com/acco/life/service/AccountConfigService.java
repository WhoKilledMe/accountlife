package com.acco.life.service;

import com.acco.life.dto.AccountConfigDto;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Flux;
import com.acco.life.common.PageResponse;

import java.util.List;

/**
 * 账户配置服务接口
 *
 * @author wensen.zhang
 * @version V1.0.0
 */
public interface AccountConfigService {
    
    /**
     * 查询所有启用的配置
     */
    Flux<AccountConfigDto> findAllActive();
    
    /**
     * 根据类型查询启用的配置
     */
    Flux<AccountConfigDto> findByType(Integer type);
    
    /**
     * 根据名称模糊查询启用的配置
     */
    Flux<AccountConfigDto> findByNameLike(String nameLike);
    
    /**
     * 获取所有配置（用于下拉选择）
     */
    Mono<List<AccountConfigDto>> getAllForSelect();

    /**
     * 分页搜索系统账户配置
     */
    Mono<PageResponse<AccountConfigDto>> page(AccountConfigDto filter, int page, int size);

    Mono<AccountConfigDto> findById(Long id);

    Mono<AccountConfigDto> save(Mono<AccountConfigDto> dtoMono);

    Mono<Void> deleteById(Long id);
}
