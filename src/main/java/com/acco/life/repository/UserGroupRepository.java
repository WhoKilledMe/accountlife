package com.acco.life.repository;

import com.acco.life.entity.UserGroup;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

public interface UserGroupRepository extends ReactiveCrudRepository<UserGroup, Integer> {
}
