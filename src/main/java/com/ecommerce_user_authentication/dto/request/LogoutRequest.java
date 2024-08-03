package com.ecommerce_user_authentication.dto.request;

public record LogoutRequest(String token, String email) {
}
