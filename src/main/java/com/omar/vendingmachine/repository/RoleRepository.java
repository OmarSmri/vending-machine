package com.omar.vendingmachine.repository;

import com.omar.vendingmachine.model.user.Role;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface RoleRepository extends MongoRepository<Role, String> {
    Role findByName(String role);
}
