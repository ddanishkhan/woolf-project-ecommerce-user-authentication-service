package com.ecommerce_user_authentication.dto.request;

import java.util.List;

public record SetUserRolesRequest(List<Long> roleIds) {
}
