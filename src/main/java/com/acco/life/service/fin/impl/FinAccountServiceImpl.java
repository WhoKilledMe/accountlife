package com.acco.life.service.fin.impl;

import com.acco.life.common.PageResponse;
import com.acco.life.dto.fin.FinAccountDto;
import com.acco.life.entity.fin.FinAccount;
import com.acco.life.enums.fin.AccountCategory;
import com.acco.life.enums.fin.AccountTypeEnum;
import com.acco.life.mapper.fin.FinAccountMapper;
import com.acco.life.repository.fin.FinAccountRepository;
import com.acco.life.service.fin.FinAccountService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 账户管理服务实现
 *
 * @author wensen.zhang
 * @version V2.0.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class FinAccountServiceImpl implements FinAccountService {

    private final FinAccountRepository accountRepository;
    private final FinAccountMapper accountMapper;

    @Override
    public Mono<FinAccountDto> create(FinAccountDto dto) {
        return generateAccountCode(dto.getUserId())
                .flatMap(code -> {
                    FinAccount entity = accountMapper.toEntity(dto);
                    entity.setAccountCode(code);
                    entity.setIsDeleted(0);
                    if (entity.getBalance() == null) {
                        entity.setBalance(BigDecimal.ZERO);
                    }
                    if (entity.getCurrency() == null) {
                        entity.setCurrency("CNY");
                    }
                    if (entity.getOwnerType() == null) {
                        entity.setOwnerType("USER");
                    }
                    if (entity.getStatus() == null) {
                        entity.setStatus("ACTIVE");
                    }
                    return accountRepository.save(entity);
                })
                .map(this::enrichDto);
    }

    @Override
    public Mono<FinAccountDto> update(FinAccountDto dto) {
        return accountRepository.findById(dto.getId())
                .flatMap(existing -> {
                    accountMapper.updateEntityFromDto(dto, existing);
                    return accountRepository.save(existing);
                })
                .map(this::enrichDto);
    }

    @Override
    public Mono<FinAccountDto> findById(Long id) {
        return accountRepository.findById(id)
                .map(this::enrichDto);
    }

    @Override
    public Mono<List<FinAccountDto>> findByUserId(Long userId) {
        return accountRepository.findByUserId(userId)
                .map(this::enrichDto)
                .collectList();
    }

    @Override
    public Mono<List<FinAccountDto>> findByUserIdAndPlatformCode(Long userId, String platformCode) {
        return accountRepository.findByUserIdAndPlatformCode(userId, platformCode)
                .map(this::enrichDto)
                .collectList();
    }

    @Override
    public Mono<FinAccountDto> findByUserIdAndPlatformCodeAndExternalRef(Long userId, String platformCode, String externalAccountRef) {
        return accountRepository.findByUserIdAndPlatformCodeAndExternalAccountRef(userId, platformCode, externalAccountRef)
                .map(this::enrichDto);
    }

    @Override
    public Mono<PageResponse<FinAccountDto>> page(Long userId, String platformCode, String accountCategory, String status, int page, int size) {
        int currentPage = Math.max(page, 0);
        int pageSize = Math.max(size, 1);
        long offset = (long) currentPage * pageSize;

        Mono<List<FinAccountDto>> dataMono = accountRepository.search(userId, platformCode, accountCategory, status, pageSize, offset)
                .map(this::enrichDto)
                .collectList();

        Mono<Long> countMono = accountRepository.countSearch(userId, platformCode, accountCategory, status);

        return Mono.zip(dataMono, countMono)
                .map(tuple -> PageResponse.of(tuple.getT1(), currentPage, pageSize, tuple.getT2()));
    }

    @Override
    public Mono<Void> deleteById(Long id) {
        return accountRepository.findById(id)
                .flatMap(entity -> {
                    entity.setIsDeleted(1);
                    return accountRepository.save(entity);
                })
                .then();
    }

    @Override
    public Mono<String> generateAccountCode(Long userId) {
        return accountRepository.findByUserId(userId)
                .count()
                .map(count -> String.format("ACCT-%d-%04d", userId, count + 1));
    }

    @Override
    public Mono<FinAccountDto> updateBalance(Long accountId, BigDecimal newBalance) {
        return accountRepository.findById(accountId)
                .flatMap(entity -> {
                    entity.setBalance(newBalance);
                    entity.setBalanceUpdatedAt(LocalDateTime.now());
                    return accountRepository.save(entity);
                })
                .map(this::enrichDto);
    }

    /**
     * 丰富 DTO 的展示字段
     */
    private FinAccountDto enrichDto(FinAccount entity) {
        FinAccountDto dto = accountMapper.toDto(entity);
        
        // 设置账户大类名称
        AccountCategory category = AccountCategory.fromCode(entity.getAccountCategory());
        if (category != null) {
            dto.setAccountCategoryName(category.getName());
        }
        
        // 设置账户类型名称
        AccountTypeEnum accountType = AccountTypeEnum.fromCode(entity.getAccountType());
        if (accountType != null) {
            dto.setAccountTypeName(accountType.getName());
        }
        
        return dto;
    }
}
