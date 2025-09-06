package com.acco.life.repository;

import com.acco.life.entity.UserGroup;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

/**
 * description: 用户组仓库接口，提供用户组的数据库操作
 *
 * @date: 2025-07-24 17:54:23
 * @author wensen.zhang
 * @version V1.0.0
 */
public interface UserGroupRepository extends ReactiveCrudRepository<UserGroup, Long> {
}
