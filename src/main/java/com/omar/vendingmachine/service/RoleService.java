package com.omar.vendingmachine.service;

import com.omar.vendingmachine.model.user.ERole;
import com.omar.vendingmachine.model.user.Role;
import com.omar.vendingmachine.repository.RoleRepository;
import com.omar.vendingmachine.repository.custom.CustomRoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RoleService {
    @Autowired
    CustomRoleRepository customRoleRepository;
    @Autowired
    RoleRepository roleRepository;

    public void createRole(ERole roleName) {
        if (findRoleByName(roleName) != null) {
            Role role = new Role(null, roleName.name());
            roleRepository.save(role);
        }
    }

    public Role findRoleByName(ERole name) {
        return roleRepository.findByName(name.name());
    }
}
