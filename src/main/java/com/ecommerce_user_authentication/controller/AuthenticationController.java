package com.ecommerce_user_authentication.controller;

import com.ecommerce_user_authentication.dto.UserDto;
import com.ecommerce_user_authentication.dto.request.LoginRequest;
import com.ecommerce_user_authentication.dto.request.LogoutRequest;
import com.ecommerce_user_authentication.dto.request.SignUpRequest;
import com.ecommerce_user_authentication.dto.request.ValidateTokenRequest;
import com.ecommerce_user_authentication.model.SessionStatus;
import com.ecommerce_user_authentication.service.AuthenticationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthenticationController {

    private final AuthenticationService authenticationService;

    public AuthenticationController(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    @PostMapping("/login")
    public ResponseEntity<UserDto> login(@RequestBody LoginRequest request) {
        return authenticationService.login(request.email(), request.password());
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@RequestBody LogoutRequest request) {
        authenticationService.logout(request.token(), request.email());
        return ResponseEntity.status(HttpStatus.RESET_CONTENT).build();
    }

    @PostMapping("/signup")
    public ResponseEntity<UserDto> signUp(@RequestBody SignUpRequest request) {
        UserDto userDto = authenticationService.signUp(request.email(), request.password());
        return ResponseEntity.ok(userDto);
    }

    @PostMapping("/validate")
    public ResponseEntity<SessionStatus> validateToken(ValidateTokenRequest request) {
        var sessionStatus = authenticationService.validate(request.token(), request.email());
        return ResponseEntity.ok(sessionStatus);
    }

}