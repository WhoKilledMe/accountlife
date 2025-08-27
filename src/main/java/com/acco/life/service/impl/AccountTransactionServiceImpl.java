package com.acco.life.service.impl;

import com.acco.life.dto.AccountTransactionDto;
import com.acco.life.common.PageResponse;
import com.acco.life.mapper.AccountTransactionMapper;
import com.acco.life.repository.AccountTransactionRepository;
import com.acco.life.service.AccountTransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * description: 账户交易服务实现类，实现账户交易的增删改查业务逻辑
 *
 * @date: 2025-07-24 17:54:23
 * @author wensen.zhang
 * @version V1.0.0
 */

@Service
@RequiredArgsConstructor
public class AccountTransactionServiceImpl implements AccountTransactionService {

    private final AccountTransactionRepository repository;
    

    private final AccountTransactionMapper mapper;


    @Override
    public Mono<List<AccountTransactionDto>> findAll() {
        return repository.findAll()
                .map(mapper::toDto).collectList();
    }

    @Override
    public Mono<AccountTransactionDto> findById(Integer id) {
        return repository.findById(id)
        .map(mapper::toDto);
    }

    @Override
    public Mono<AccountTransactionDto> save(Mono<AccountTransactionDto> dto) {

        return dto.map(mapper::toEntity)
            .flatMap(repository::save)
            .map(mapper::toDto);
    }

    @Override
    public Mono<Void> deleteById(Integer id) {
        return repository.deleteById(id);
    }

    @Override
    public Mono<PageResponse<AccountTransactionDto>> page(AccountTransactionDto filter, int page, int size) {
        if (filter == null) {
            filter = new AccountTransactionDto();
        }
        final int currentPage = Math.max(page, 0);
        final int pageSize = Math.max(size, 1);
        long offset = (long) currentPage * pageSize;
        long limit = pageSize;

        String likeDesc = (filter.getDescription() == null || filter.getDescription().isEmpty()) ? null : "%" + filter.getDescription() + "%";
        Integer userId = filter.getUserId();
        Integer accountId = filter.getAccountId();

        Mono<List<AccountTransactionDto>> dataMono = repository.search(likeDesc, userId, accountId, limit, offset)
                .map(mapper::toDto)
                .collectList();

        Mono<Long> countMono = repository.countSearch(likeDesc, userId, accountId);

        return Mono.zip(dataMono, countMono)
                .map(tuple -> PageResponse.of(tuple.getT1(), currentPage, pageSize, tuple.getT2()));
    }
}
