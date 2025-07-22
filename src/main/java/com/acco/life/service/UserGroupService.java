package com.acco.life.service;

import com.acco.life.dto.UserGroupDto;
import reactor.core.publisher.Mono;

import java.util.List;

public interface UserGroupService {

     Mono<List<UserGroupDto>> findAll();

     Mono<UserGroupDto> findById(Integer id);

     Mono<UserGroupDto> save(Mono<UserGroupDto> entity);

     Mono<Void> deleteById(Integer id);
}