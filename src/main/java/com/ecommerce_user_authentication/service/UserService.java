package com.ecommerce_user_authentication.service;

import com.ecommerce_user_authentication.dto.UserDto;
import com.ecommerce_user_authentication.exception.InvalidRoleException;
import com.ecommerce_user_authentication.exception.UserNotFoundException;
import com.ecommerce_user_authentication.model.RoleEnum;
import com.ecommerce_user_authentication.model.UserEntity;
import com.ecommerce_user_authentication.repository.RoleRepository;
import com.ecommerce_user_authentication.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

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

    public UserDto setUserRole(Long userId, RoleEnum role) {
        UserEntity user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException("User not found"));
        var roleEntity = roleRepository.findByName(role).orElseThrow(InvalidRoleException::new);
        user.setRoleEntity(roleEntity);
        var savedEntity = userRepository.save(user);
        return UserDto.from(savedEntity);
    }
}
