package com.acco.life.service.impl;

import com.acco.life.dto.AccountTransactionDto;
import com.acco.life.common.PageResponse;
import com.acco.life.mapper.AccountTransactionMapper;
import com.acco.life.repository.AccountTransactionRepository;
import com.acco.life.repository.AssetAccountRepository;
import com.acco.life.repository.TransactionCategoryRepository;
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
    private final TransactionCategoryRepository categoryRepository;
    private final AssetAccountRepository assetAccountRepository;

    private final AccountTransactionMapper mapper;


    @Override
    public Mono<List<AccountTransactionDto>> findAll() {
        return repository.findAll()
                .map(mapper::toDto)
                .flatMap(dto -> enrichWithCategoryAndAccountName(dto))
                .collectList();
    }

    @Override
    public Mono<AccountTransactionDto> findById(Integer id) {
        return repository.findById(id)
        .map(mapper::toDto)
        .flatMap(dto -> enrichWithCategoryAndAccountName(dto));
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

        String likeDesc = (filter.getDescription() == null || filter.getDescription().isEmpty()) ? null : "%" + filter.getDescription() + "%";
        Integer userId = filter.getUserId();
        Integer accountId = filter.getAccountId();

        Mono<List<AccountTransactionDto>> dataMono = repository.search(likeDesc, userId, accountId, pageSize, offset)
                .map(mapper::toDto)
                .flatMap(dto -> enrichWithCategoryAndAccountName(dto))
                .collectList();

        Mono<Long> countMono = repository.countSearch(likeDesc, userId, accountId);

        return Mono.zip(dataMono, countMono)
                .map(tuple -> PageResponse.of(tuple.getT1(), currentPage, pageSize, tuple.getT2()));
    }

    /**
     * 为AccountTransactionDto补充分类名称和账户名称
     */
    private Mono<AccountTransactionDto> enrichWithCategoryAndAccountName(AccountTransactionDto dto) {
        Mono<AccountTransactionDto> categoryMono = dto.getCategoryId() == null 
            ? Mono.just(dto)
            : categoryRepository.findById(dto.getCategoryId())
                .map(cat -> { dto.setCategoryName(cat.getName()); return dto; })
                .defaultIfEmpty(dto);

        Mono<AccountTransactionDto> accountMono = dto.getAccountId() == null || dto.getUserId() == null
            ? Mono.just(dto)
            : assetAccountRepository.findByUserIdAndId(dto.getUserId(), dto.getAccountId())
                .map(account -> { dto.setAccountName(account.getName()); return dto; })
                .defaultIfEmpty(dto);

        return categoryMono.flatMap(categoryDto -> 
            accountMono.map(accountDto -> {
                // 合并两个DTO的信息
                if (categoryDto.getCategoryName() != null) {
                    accountDto.setCategoryName(categoryDto.getCategoryName());
                }
                return accountDto;
            })
        );
    }
}
