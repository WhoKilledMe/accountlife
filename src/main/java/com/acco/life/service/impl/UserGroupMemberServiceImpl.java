package com.acco.life.service.impl;

import com.acco.life.dto.UserGroupMemberDto;
import com.acco.life.mapper.UserGroupMemberMapper;
import com.acco.life.repository.UserGroupMemberRepository;
import com.acco.life.service.UserGroupMemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class UserGroupMemberServiceImpl implements UserGroupMemberService {

    private final UserGroupMemberRepository repository;

    private final UserGroupMemberMapper mapper;


    @Override
    public Flux<UserGroupMemberDto> findAll() {
        return repository.findAll()
    .map(mapper::toDto);
    }

    @Override
    public Mono<UserGroupMemberDto> findById(Integer id) {
        return repository.findById(id)
        .map(mapper::toDto);
    }

    @Override
    public Mono<UserGroupMemberDto> save(Mono<UserGroupMemberDto> dto) {

        return dto.map(mapper::toEntity)
            .flatMap(repository::save)
            .map(mapper::toDto);
    }

    @Override
    public Mono<Void> deleteById(Integer id) {
        return repository.deleteById(id);
    }
}
