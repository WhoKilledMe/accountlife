package com.acco.life.service;

import com.acco.life.dto.UserGroupMemberDto;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface UserGroupMemberService {

     Flux<UserGroupMemberDto> findAll();

     Mono<UserGroupMemberDto> findById(Integer id);

     Mono<UserGroupMemberDto> save(Mono<UserGroupMemberDto> entity);

     Mono<Void> deleteById(Integer id);
}