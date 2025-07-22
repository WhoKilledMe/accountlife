package com.acco.life.repository;

import com.acco.life.entity.User;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

public interface UserAccountRepository extends ReactiveCrudRepository<User, Integer> {
}
