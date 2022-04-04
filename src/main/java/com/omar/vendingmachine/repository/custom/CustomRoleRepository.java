package com.omar.vendingmachine.repository.custom;

import com.omar.vendingmachine.model.user.ERole;
import com.omar.vendingmachine.model.user.Role;
import reactor.core.publisher.Flux;

public interface CustomRoleRepository {
    Flux<Role> findByName(ERole name);
}
