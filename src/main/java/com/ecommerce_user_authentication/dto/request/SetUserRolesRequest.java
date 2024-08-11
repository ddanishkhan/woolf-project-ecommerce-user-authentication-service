package com.ecommerce_user_authentication.dto.request;

import com.ecommerce_user_authentication.model.RoleEnum;
import jakarta.validation.constraints.NotNull;

public record SetUserRolesRequest(@NotNull RoleEnum role) {
}
