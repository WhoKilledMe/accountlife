package com.acco.life.service;

import com.acco.life.dto.UserDto;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface UserService {

     Flux<UserDto> findAll();

     Mono<UserDto> findById(Integer id);

     Mono<UserDto> save(Mono<UserDto> entity);

     Mono<Void> deleteById(Integer id);
}