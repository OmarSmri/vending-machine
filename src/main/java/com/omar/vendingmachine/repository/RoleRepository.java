package com.omar.vendingmachine.repository;

import com.omar.vendingmachine.model.user.ERole;
import com.omar.vendingmachine.model.user.Role;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;

public interface RoleRepository extends MongoRepository<Role, String> {
    Role findByName(String role);
}
