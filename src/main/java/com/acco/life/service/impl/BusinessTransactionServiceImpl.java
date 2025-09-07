package com.acco.life.service.impl;

import com.acco.life.dto.BusinessTransactionDto;
import com.acco.life.common.PageResponse;
import com.acco.life.mapper.BusinessTransactionMapper;
import com.acco.life.repository.BusinessTransactionRepository;
import com.acco.life.repository.TransactionCategoryRepository;
import com.acco.life.service.BusinessTransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * description: 业务交易服务实现类，实现业务交易的增删改查业务逻辑
 *
 * @date: 2025-01-15 10:00:00
 * @author wensen.zhang
 * @version V1.0.0
 */

@Service
@RequiredArgsConstructor
public class BusinessTransactionServiceImpl implements BusinessTransactionService {

    private final BusinessTransactionRepository repository;
    private final TransactionCategoryRepository categoryRepository;
    private final BusinessTransactionMapper businessTransactionMapper;

    @Override
    public Mono<List<BusinessTransactionDto>> findAll() {
        return repository.findAll()
                .map(businessTransactionMapper::toDto)
                .flatMap(this::enrichWithCategoryName)
                .collectList();
    }

    @Override
    public Mono<BusinessTransactionDto> findById(Long id) {
        return repository.findById(id)
        .map(businessTransactionMapper::toDto)
        .flatMap(dto -> enrichWithCategoryName(dto));
    }

    @Override
    public Mono<BusinessTransactionDto> save(Mono<BusinessTransactionDto> dto) {
        return dto.map(businessTransactionMapper::toEntity)
            .flatMap(repository::save)
            .map(businessTransactionMapper::toDto);
    }

    @Override
    public Mono<Void> deleteById(Long id) {
        return repository.deleteById(id);
    }

    @Override
    public Mono<PageResponse<BusinessTransactionDto>> page(BusinessTransactionDto filter, int page, int size) {
        if (filter == null) {
            filter = new BusinessTransactionDto();
        }
        final int currentPage = Math.max(page, 0);
        final int pageSize = Math.max(size, 1);
        long offset = (long) currentPage * pageSize;

        Long userId = filter.getUserId();
        Long categoryId = filter.getCategoryId();

        Mono<List<BusinessTransactionDto>> dataMono = repository.search(userId, categoryId, pageSize, offset)
                .map(businessTransactionMapper::toDto)
                .flatMap(dto -> enrichWithCategoryName(dto))
                .collectList();

        Mono<Long> countMono = repository.countSearch(userId, categoryId);

        return Mono.zip(dataMono, countMono)
                .map(tuple -> PageResponse.of(tuple.getT1(), currentPage, pageSize, tuple.getT2()));
    }

    /**
     * 为BusinessTransactionDto补充分类名称
     */
    private Mono<BusinessTransactionDto> enrichWithCategoryName(BusinessTransactionDto dto) {
        return dto.getCategoryId() == null
            ? Mono.just(dto)
            : categoryRepository.findById(dto.getCategoryId())
                .map(cat -> { dto.setCategoryName(cat.getName()); return dto; })
                .defaultIfEmpty(dto);
    }
}
