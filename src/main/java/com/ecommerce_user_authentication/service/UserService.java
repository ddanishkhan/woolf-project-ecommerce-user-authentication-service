package com.ecommerce_user_authentication.service;

import com.ecommerce_user_authentication.dto.UserDto;
import com.ecommerce_user_authentication.model.RoleEntity;
import com.ecommerce_user_authentication.model.UserEntity;
import com.ecommerce_user_authentication.repository.RoleRepository;
import com.ecommerce_user_authentication.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    public Optional<UserDto> getUserDetails(Long userId) {
        var userEntity = userRepository.findById(userId);
        return userEntity.map(UserDto::from);
    }

    public Optional<UserDto> getUserDetailsByUserEmail(String email) {
        var userEntity = userRepository.findOneByEmail(email);
        return userEntity.map(UserDto::from);
    }

    public UserDto addUserRoles(Long userId, List<Long> roleIds) {

        UserEntity user = userRepository.findById(userId).orElseThrow(() -> new EntityNotFoundException("User not found"));

        List<RoleEntity> roles = roleRepository.findAllById(roleIds);
        user.getRoleEntities().addAll(roles);

        var savedEntity = userRepository.save(user);

        return UserDto.from(savedEntity);
    }
}
