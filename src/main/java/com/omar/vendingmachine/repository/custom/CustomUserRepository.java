package com.omar.vendingmachine.repository.custom;

import com.omar.vendingmachine.model.user.User;
import reactor.core.publisher.Flux;

public interface CustomUserRepository {
    Flux<User> findByUsername(String username);
}
