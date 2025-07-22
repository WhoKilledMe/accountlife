package com.acco.life.repository;

import com.acco.life.entity.UserGroupMember;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

public interface UserGroupMemberRepository extends ReactiveCrudRepository<UserGroupMember, Integer> {
}
