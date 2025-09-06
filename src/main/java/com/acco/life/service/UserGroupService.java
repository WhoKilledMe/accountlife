package com.acco.life.service;

import com.acco.life.dto.UserGroupDto;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * description: 用户组服务接口，定义用户组的增删改查操作
 *
 * @date: 2025-07-24 17:54:23
 * @author wensen.zhang
 * @version V1.0.0
 */
public interface UserGroupService {

     Mono<List<UserGroupDto>> findAll();

     Mono<UserGroupDto> findById(Long id);

     Mono<UserGroupDto> save(Mono<UserGroupDto> entity);

     Mono<Void> deleteById(Long id);
}