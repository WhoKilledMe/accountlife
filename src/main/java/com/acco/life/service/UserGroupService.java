package com.acco.life.service;

import com.acco.life.dto.UserGroupDto;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface UserGroupService {

     Flux<UserGroupDto> findAll();

     Mono<UserGroupDto> findById(Integer id);

     Mono<UserGroupDto> save(Mono<UserGroupDto> entity);

     Mono<Void> deleteById(Integer id);
}