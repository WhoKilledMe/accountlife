package com.acco.life.service.fin.impl;

import com.acco.life.common.PageResponse;
import com.acco.life.dto.fin.FinPlatformDto;
import com.acco.life.entity.fin.FinPlatform;
import com.acco.life.mapper.fin.FinPlatformMapper;
import com.acco.life.repository.fin.FinPlatformRepository;
import com.acco.life.service.fin.FinPlatformService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * 平台字典管理服务实现
 *
 * @author wensen.zhang
 * @version V2.0.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class FinPlatformServiceImpl implements FinPlatformService {

    private final FinPlatformRepository platformRepository;
    private final FinPlatformMapper platformMapper;

    @Override
    public Mono<FinPlatformDto> create(FinPlatformDto dto) {
        // 检查平台代码是否已存在
        return platformRepository.findByPlatformCode(dto.getPlatformCode())
                .flatMap(existing -> Mono.<FinPlatform>error(new IllegalArgumentException("平台代码已存在: " + dto.getPlatformCode())))
                .switchIfEmpty(Mono.defer(() -> {
                    FinPlatform entity = platformMapper.toEntity(dto);
                    // 设置默认值
                    if (entity.getSupportPayment() == null) {
                        entity.setSupportPayment(false);
                    }
                    if (entity.getSupportCredit() == null) {
                        entity.setSupportCredit(false);
                    }
                    if (entity.getSupportBalance() == null) {
                        entity.setSupportBalance(false);
                    }
                    if (entity.getSupportBill() == null) {
                        entity.setSupportBill(true);
                    }
                    if (entity.getSortOrder() == null) {
                        entity.setSortOrder(0);
                    }
                    if (entity.getStatus() == null) {
                        entity.setStatus("ACTIVE");
                    }
                    entity.setIsDeleted(0);
                    return platformRepository.save(entity);
                }))
                .map(this::enrichDto);
    }

    @Override
    public Mono<FinPlatformDto> update(FinPlatformDto dto) {
        return platformRepository.findById(dto.getId())
                .switchIfEmpty(Mono.error(new IllegalArgumentException("平台不存在: " + dto.getId())))
                .flatMap(existing -> {
                    // 如果修改了平台代码，检查新代码是否已被其他记录使用
                    if (!existing.getPlatformCode().equals(dto.getPlatformCode())) {
                        return platformRepository.findByPlatformCode(dto.getPlatformCode())
                                .flatMap(conflict -> {
                                    if (!conflict.getId().equals(dto.getId())) {
                                        return Mono.error(new IllegalArgumentException("平台代码已被使用: " + dto.getPlatformCode()));
                                    }
                                    return Mono.just(existing);
                                })
                                .switchIfEmpty(Mono.just(existing));
                    }
                    return Mono.just(existing);
                })
                .flatMap(existing -> {
                    platformMapper.updateEntityFromDto(dto, existing);
                    return platformRepository.save(existing);
                })
                .map(this::enrichDto);
    }

    @Override
    public Mono<FinPlatformDto> findById(Long id) {
        return platformRepository.findById(id)
                .map(this::enrichDto);
    }

    @Override
    public Mono<FinPlatformDto> findByPlatformCode(String platformCode) {
        return platformRepository.findByPlatformCode(platformCode)
                .map(this::enrichDto);
    }

    @Override
    public Mono<List<FinPlatformDto>> findAllActive() {
        return platformRepository.findAllActive()
                .map(this::enrichDto)
                .collectList();
    }

    @Override
    public Mono<List<FinPlatformDto>> findAll() {
        return platformRepository.findAll()
                .filter(platform -> platform.getIsDeleted() == null || platform.getIsDeleted() == 0)
                .map(this::enrichDto)
                .collectList();
    }

    @Override
    public Mono<List<FinPlatformDto>> findByPlatformType(String platformType) {
        return platformRepository.findByPlatformType(platformType)
                .filter(platform -> platform.getIsDeleted() == null || platform.getIsDeleted() == 0)
                .map(this::enrichDto)
                .collectList();
    }

    @Override
    public Mono<PageResponse<FinPlatformDto>> page(String platformType, String status, int page, int size) {
        int offset = page * size;
        
        Mono<Long> countMono = platformRepository.countSearch(platformType, status);
        Flux<FinPlatformDto> dataFlux = platformRepository.findPage(platformType, status, size, offset)
                .map(this::enrichDto);

        return Mono.zip(countMono, dataFlux.collectList())
                .map(tuple -> {
                    long total = tuple.getT1();
                    List<FinPlatformDto> data = tuple.getT2();
                    return PageResponse.of(data, page, size, total);
                });
    }

    @Override
    public Mono<Void> deleteById(Long id) {
        return platformRepository.findById(id)
                .switchIfEmpty(Mono.error(new IllegalArgumentException("平台不存在: " + id)))
                .flatMap(platform -> {
                    platform.setIsDeleted(1);
                    return platformRepository.save(platform);
                })
                .then();
    }

    /**
     * 丰富 DTO 信息（添加扩展字段）
     */
    private FinPlatformDto enrichDto(FinPlatform entity) {
        FinPlatformDto dto = platformMapper.toDto(entity);
        // 可以在这里添加扩展字段，如 platformTypeName 等
        if (dto.getPlatformType() != null) {
            // 可以根据需要添加类型名称映射
            dto.setPlatformTypeName(dto.getPlatformType());
        }
        return dto;
    }
}
