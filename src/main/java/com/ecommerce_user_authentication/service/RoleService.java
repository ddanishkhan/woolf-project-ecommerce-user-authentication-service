package com.ecommerce_user_authentication.service;

import com.ecommerce_user_authentication.dto.response.RoleCreatedResponse;
import com.ecommerce_user_authentication.model.RoleEntity;
import com.ecommerce_user_authentication.repository.RoleRepository;
import org.springframework.stereotype.Service;

@Service
public class RoleService {

    private final RoleRepository roleRepository;

    public RoleService(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    public RoleCreatedResponse createRole(String name) {
        var roleEntity = new RoleEntity();
        roleEntity.setName(name);
        var savedRoleEntity = roleRepository.save(roleEntity);
        return new RoleCreatedResponse(savedRoleEntity.getName());
    }
}
