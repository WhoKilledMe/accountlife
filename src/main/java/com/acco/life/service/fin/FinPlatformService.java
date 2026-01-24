package com.acco.life.service.fin;

import com.acco.life.common.PageResponse;
import com.acco.life.dto.fin.FinPlatformDto;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * 平台字典管理服务接口
 *
 * @author wensen.zhang
 * @version V2.0.0
 */
public interface FinPlatformService {

    /**
     * 创建平台
     */
    Mono<FinPlatformDto> create(FinPlatformDto dto);

    /**
     * 更新平台
     */
    Mono<FinPlatformDto> update(FinPlatformDto dto);

    /**
     * 根据ID查询平台
     */
    Mono<FinPlatformDto> findById(Long id);

    /**
     * 根据平台代码查询
     */
    Mono<FinPlatformDto> findByPlatformCode(String platformCode);

    /**
     * 查询所有启用的平台
     */
    Mono<List<FinPlatformDto>> findAllActive();

    /**
     * 查询所有平台
     */
    Mono<List<FinPlatformDto>> findAll();

    /**
     * 根据平台类型查询
     */
    Mono<List<FinPlatformDto>> findByPlatformType(String platformType);

    /**
     * 分页查询
     */
    Mono<PageResponse<FinPlatformDto>> page(String platformType, String status, int page, int size);

    /**
     * 删除平台（软删除）
     */
    Mono<Void> deleteById(Long id);
}
