package com.ecommerce_user_authentication.dto;

import com.ecommerce_user_authentication.model.RoleEntity;
import com.ecommerce_user_authentication.model.UserEntity;

public record UserDto(String email, RoleEntity roleEntity) {

    public static UserDto from(UserEntity user) {
        return new UserDto(user.getEmail(), user.getRoleEntity());
    }

}
