package com.acco.life.service.impl;

import com.acco.life.dto.UserGroupDto;
import com.acco.life.mapper.UserGroupMapper;
import com.acco.life.repository.UserGroupRepository;
import com.acco.life.service.UserGroupService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class UserGroupServiceImpl implements UserGroupService {

    private final UserGroupRepository repository;

    private final UserGroupMapper mapper;


    @Override
    public Flux<UserGroupDto> findAll() {
        return repository.findAll()
    .map(mapper::toDto);
    }

    @Override
    public Mono<UserGroupDto> findById(Integer id) {
        return repository.findById(id)
        .map(mapper::toDto);
    }

    @Override
    public Mono<UserGroupDto> save(Mono<UserGroupDto> dto) {

        return dto.map(mapper::toEntity)
            .flatMap(repository::save)
            .map(mapper::toDto);
    }

    @Override
    public Mono<Void> deleteById(Integer id) {
        return repository.deleteById(id);
    }
}
