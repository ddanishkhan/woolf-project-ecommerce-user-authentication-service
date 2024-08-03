package com.ecommerce_user_authentication.dto;

import com.ecommerce_user_authentication.model.RoleEntity;
import com.ecommerce_user_authentication.model.UserEntity;

import java.util.Set;

public record UserDto(String email, Set<RoleEntity> roleEntities) {

    public static UserDto from(UserEntity user) {
        return new UserDto(user.getEmail(), user.getRoleEntities());
    }

}
