package com.ecommerce_user_authentication.dto.request;

public record ValidateTokenRequest (String token, String email){
}
