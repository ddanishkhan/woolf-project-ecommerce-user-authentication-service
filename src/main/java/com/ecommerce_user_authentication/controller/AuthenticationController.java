package com.ecommerce_user_authentication.controller;

import com.ecommerce_user_authentication.dto.UserDto;
import com.ecommerce_user_authentication.dto.request.LoginRequest;
import com.ecommerce_user_authentication.dto.request.SignUpRequest;
import com.ecommerce_user_authentication.dto.request.ValidateTokenRequest;
import com.ecommerce_user_authentication.dto.response.SessionStatusResponse;
import com.ecommerce_user_authentication.service.AuthenticationService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequestMapping("/auth")
public class AuthenticationController {

    private final AuthenticationService authenticationService;

    public AuthenticationController(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    @PostMapping("/login")
    public ResponseEntity<UserDto> login(@RequestBody @Valid LoginRequest request) {
        return authenticationService.login(request.email(), request.password());
    }

    @PostMapping("/logout/{email}")
    public ResponseEntity<Void> logout(@PathVariable String email, @RequestHeader("token") String token) {
        authenticationService.logout(token, email);
        return ResponseEntity.status(HttpStatus.RESET_CONTENT).build();
    }

    @PostMapping("/signup")
    public ResponseEntity<UserDto> signUp(@RequestBody @Valid SignUpRequest request) {
        Optional<UserDto> userDto = authenticationService.signUp(request.email(), request.password());
        return userDto.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.badRequest().build());
    }

    @PostMapping("/validate")
    public ResponseEntity<SessionStatusResponse> validateToken(@RequestBody @Valid ValidateTokenRequest request) {
        var sessionStatus = authenticationService.validate(request.token(), request.email());
        return ResponseEntity.ok(new SessionStatusResponse(sessionStatus.name()));
    }

}
