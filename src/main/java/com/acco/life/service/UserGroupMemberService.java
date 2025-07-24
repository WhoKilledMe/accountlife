package com.acco.life.service;

import com.acco.life.dto.UserGroupMemberDto;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * description: 用户组成员服务接口，定义用户组成员的增删改查操作
 *
 * @date: 2025-07-24 17:54:23
 * @author wensen.zhang
 * @version V1.0.0
 */
public interface UserGroupMemberService {

     Mono<List<UserGroupMemberDto>> findAll();

     Mono<UserGroupMemberDto> findById(Integer id);

     Mono<UserGroupMemberDto> save(Mono<UserGroupMemberDto> entity);

     Mono<Void> deleteById(Integer id);
}