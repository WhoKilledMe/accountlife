package com.acco.life.repository;

import com.acco.life.entity.UserGroupMember;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

/**
 * description: 用户组成员仓库接口，提供用户组成员的数据库操作
 *
 * @date: 2025-07-24 17:54:23
 * @author wensen.zhang
 * @version V1.0.0
 */
public interface UserGroupMemberRepository extends ReactiveCrudRepository<UserGroupMember, Integer> {
}
