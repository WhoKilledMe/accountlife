package com.acco.life.service.impl;

import com.acco.life.common.PageResponse;
import com.acco.life.dto.UserMailConfigDto;
import com.acco.life.mapper.UserMailConfigMapper;
import com.acco.life.repository.UserMailConfigRepository;
import com.acco.life.service.UserMailConfigService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.List;
import java.net.InetSocketAddress;
import java.net.Socket;

@Service
@RequiredArgsConstructor
public class UserMailConfigServiceImpl implements UserMailConfigService {

    private final UserMailConfigRepository repository;
    private final UserMailConfigMapper mapper;

    @Override
    public Mono<List<UserMailConfigDto>> findAll() {
        return repository.findAll().map(mapper::toDto).collectList();
    }

    @Override
    public Mono<UserMailConfigDto> findById(Long id) {
        return repository.findById(id).map(mapper::toDto);
    }

    @Override
    public Mono<UserMailConfigDto> save(Mono<UserMailConfigDto> dto) {
        return dto.map(mapper::toEntity)
                .flatMap(repository::save)
                .map(mapper::toDto);
    }

    @Override
    public Mono<Void> deleteById(Long id) {
        return repository.deleteById(id);
    }

    @Override
    public Mono<PageResponse<UserMailConfigDto>> page(UserMailConfigDto filter, int page, int size) {
        if (filter == null) filter = new UserMailConfigDto();
        final int currentPage = Math.max(page, 0);
        final int pageSize = Math.max(size, 1);
        long offset = (long) currentPage * pageSize;

        Long userId = filter.getUserId();
        String likeName = (filter.getName() == null || filter.getName().isEmpty()) ? null : "%" + filter.getName() + "%";

        Mono<List<UserMailConfigDto>> dataMono = repository.search(userId, likeName, pageSize, offset)
                .map(mapper::toDto)
                .collectList();

        Mono<Long> countMono = repository.countSearch(userId, likeName);

        return Mono.zip(dataMono, countMono)
                .map(tuple -> PageResponse.of(tuple.getT1(), currentPage, pageSize, tuple.getT2()));
    }

    @Override
    public Mono<List<UserMailConfigDto>> query(UserMailConfigDto filter) {
        if (filter == null) filter = new UserMailConfigDto();
        Long userId = filter.getUserId();
        String likeName = (filter.getName() == null || filter.getName().isEmpty()) ? null : "%" + filter.getName() + "%";
        // reuse repository search with a large limit for simple query
        long limit = 100L;
        long offset = 0L;
        return repository.search(userId, likeName, limit, offset)
                .map(mapper::toDto)
                .collectList();
    }

    @Override
    public Mono<UserMailConfigDto> findOneByUserId(Long userId) {
        long limit = 1L;
        long offset = 0L;
        return repository.search(userId, null, limit, offset)
                .next()
                .map(mapper::toDto);
    }

    @Override
    public Mono<Boolean> testConnectivity(UserMailConfigDto config) {
        return Mono.fromCallable(() -> {
            String host = config.getHost();
            Integer port = config.getPort();
            int timeoutMs = config.getConnectionTimeout() != null ? config.getConnectionTimeout() : 5000;
            if (host == null || port == null) {
                return false;
            }
            try (Socket socket = new Socket()) {
                socket.connect(new InetSocketAddress(host, port), timeoutMs);
                return socket.isConnected();
            } catch (Exception e) {
                return false;
            }
        });
    }
}


