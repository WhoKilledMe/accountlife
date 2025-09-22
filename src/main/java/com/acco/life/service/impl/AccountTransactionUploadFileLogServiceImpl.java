package com.acco.life.service.impl;

import com.acco.life.common.PageResponse;
import com.acco.life.dto.AccountTransactionUploadFileLogDto;
import com.acco.life.mapper.AccountTransactionUploadFileLogMapper;
import com.acco.life.repository.UserAccountRepository;
import com.acco.life.repository.UserRepository;
import com.acco.life.repository.AccountTransactionUploadFileLogRepository;
import com.acco.life.service.AccountTransactionUploadFileLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AccountTransactionUploadFileLogServiceImpl implements AccountTransactionUploadFileLogService {

    private final AccountTransactionUploadFileLogRepository repository;
    private final AccountTransactionUploadFileLogMapper accountTransactionUploadFileLogMapper;
    private final UserRepository userRepository;
    private final UserAccountRepository userAccountRepository;

    @Override
    public Mono<List<AccountTransactionUploadFileLogDto>> findAll() {
        return repository.findAll().map(accountTransactionUploadFileLogMapper::toDto).collectList();
    }

    @Override
    public Mono<AccountTransactionUploadFileLogDto> findById(Long id) {
        return repository.findById(id).map(accountTransactionUploadFileLogMapper::toDto);
    }

    @Override
    public Mono<AccountTransactionUploadFileLogDto> save(Mono<AccountTransactionUploadFileLogDto> dto) {
        return dto.map(accountTransactionUploadFileLogMapper::toEntity)
                .flatMap(repository::save)
                .map(accountTransactionUploadFileLogMapper::toDto);
    }

    @Override
    public Mono<Void> deleteById(Long id) {
        return repository.deleteById(id);
    }

    @Override
    public Mono<PageResponse<AccountTransactionUploadFileLogDto>> page(AccountTransactionUploadFileLogDto filter, int page, int size) {
        if (filter == null) {
            filter = new AccountTransactionUploadFileLogDto();
        }
        final int currentPage = Math.max(page, 0);
        final int pageSize = Math.max(size, 1);
        long offset = (long) currentPage * pageSize;

        String status = (filter.getStatus() == null || filter.getStatus().isEmpty()) ? null : filter.getStatus();
        Long userId = filter.getUserId();
        String fileNameLike = (filter.getFileName() == null || filter.getFileName().isEmpty()) ? null : "%" + filter.getFileName() + "%";

        Mono<List<AccountTransactionUploadFileLogDto>> dataMono = repository.search(status, userId, fileNameLike, filter.getAccountId(), pageSize, offset)
                .map(accountTransactionUploadFileLogMapper::toDto)
                .flatMap(this::enrichNames)
                .collectList();

        Mono<Long> countMono = repository.countSearch(status, userId, fileNameLike, filter.getAccountId());

        return Mono.zip(dataMono, countMono)
                .map(tuple -> PageResponse.of(tuple.getT1(), currentPage, pageSize, tuple.getT2()));
    }

    private Mono<AccountTransactionUploadFileLogDto> enrichNames(AccountTransactionUploadFileLogDto dto) {
        Mono<AccountTransactionUploadFileLogDto> userMono = dto.getUserId() == null
                ? Mono.just(dto)
                : userRepository.findById(dto.getUserId())
                .map(user -> {
                    dto.setUserName(user.getUsername());
                    return dto;
                })
                .defaultIfEmpty(dto);

        Mono<AccountTransactionUploadFileLogDto> accountMono = dto.getAccountId() == null || dto.getUserId() == null
                ? Mono.just(dto)
                : userAccountRepository.findByUserIdAndId(dto.getUserId(), dto.getAccountId())
                .map(acc -> {
                    dto.setAccountName(acc.getName());
                    return dto;
                })
                .defaultIfEmpty(dto);

        return userMono.flatMap(accountMono::thenReturn);
    }
}


