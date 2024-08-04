package com.ecommerce_user_authentication.service;

import com.ecommerce_user_authentication.dto.response.RoleCreatedResponse;
import com.ecommerce_user_authentication.model.RoleEntity;
import com.ecommerce_user_authentication.repository.RoleRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class RoleService {

    private final RoleRepository roleRepository;

    public RoleService(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    public Optional<RoleCreatedResponse> createRole(String name) {
        var roleEntity = new RoleEntity();
        roleEntity.setName(name);
        if (roleRepository.existsByName(name)){
            return Optional.empty();
        }
        var savedRoleEntity = roleRepository.save(roleEntity);
        return Optional.of(new RoleCreatedResponse(savedRoleEntity.getId(), savedRoleEntity.getName()));
    }
}
