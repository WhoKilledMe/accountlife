package com.acco.life.service;

import com.acco.life.dto.UserDto;
import reactor.core.publisher.Mono;

import java.util.List;

public interface UserService {

     Mono<List<UserDto>> findAll();

     Mono<UserDto> findById(Integer id);

     Mono<UserDto> save(Mono<UserDto> entity);

     Mono<Void> deleteById(Integer id);
}