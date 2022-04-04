package com.omar.vendingmachine.repository;

import com.omar.vendingmachine.model.user.User;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

import java.math.BigInteger;

public interface UserRepository extends ReactiveMongoRepository<User, String> {
    User findByUsername(String username);
}
