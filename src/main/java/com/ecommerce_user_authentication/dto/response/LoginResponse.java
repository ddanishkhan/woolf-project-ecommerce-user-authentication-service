package com.ecommerce_user_authentication.dto.response;

public record LoginResponse(String token, Long expiresIn) {
}
