package com.acco.life.service;

import com.acco.life.dto.UserGroupMemberDto;
import reactor.core.publisher.Mono;

import java.util.List;

public interface UserGroupMemberService {

     Mono<List<UserGroupMemberDto>> findAll();

     Mono<UserGroupMemberDto> findById(Integer id);

     Mono<UserGroupMemberDto> save(Mono<UserGroupMemberDto> entity);

     Mono<Void> deleteById(Integer id);
}