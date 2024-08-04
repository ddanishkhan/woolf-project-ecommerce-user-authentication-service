package com.ecommerce_user_authentication.dto.request;

import jakarta.validation.constraints.NotNull;

import java.util.List;

public record SetUserRolesRequest(@NotNull List<Long> roleIds) {
}
