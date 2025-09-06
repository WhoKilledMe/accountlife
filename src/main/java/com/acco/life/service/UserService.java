package com.acco.life.service;

import com.acco.life.dto.UserDto;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * description: 用户服务接口，定义用户相关业务操作方法
 *
 * @date: 2025-07-24 17:54:23
 * @author wensen.zhang
 * @version V1.0.0
 */
public interface UserService {

     Mono<List<UserDto>> findAll();

     Mono<UserDto> findById(Long id);

     Mono<UserDto> save(Mono<UserDto> entity);

     Mono<Void> deleteById(Long id);
}