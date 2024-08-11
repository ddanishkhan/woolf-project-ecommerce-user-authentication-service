package com.ecommerce_user_authentication.controller;

import com.ecommerce_user_authentication.dto.UserDto;
import com.ecommerce_user_authentication.dto.request.LoginRequest;
import com.ecommerce_user_authentication.dto.request.SignUpRequest;
import com.ecommerce_user_authentication.dto.request.ValidateTokenRequest;
import com.ecommerce_user_authentication.dto.response.LoginResponse;
import com.ecommerce_user_authentication.dto.response.SessionStatusResponse;
import com.ecommerce_user_authentication.model.UserEntity;
import com.ecommerce_user_authentication.service.AuthenticationService;
import com.ecommerce_user_authentication.service.JwtService;
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
    private final JwtService jwtService;

    public AuthenticationController(AuthenticationService authenticationService, JwtService jwtService) {
        this.authenticationService = authenticationService;
        this.jwtService = jwtService;
    }

    @PostMapping("/login")
    public ResponseEntity<UserDto> login(@RequestBody @Valid LoginRequest request) {
        return authenticationService.login(request.email(), request.password());
    }

    @PostMapping("/v2/login")
    public ResponseEntity<LoginResponse> loginV2(@RequestBody @Valid LoginRequest request) {
        var authenticatedUser = authenticationService.authenticate(request.email(), request.password());
        String jwtToken = jwtService.generateToken(authenticatedUser);

        LoginResponse loginResponse = new LoginResponse(jwtToken, jwtService.getExpirationTime());

        return ResponseEntity.ok(loginResponse);
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

    @PostMapping("/v2/signup")
    public ResponseEntity<UserEntity> signUpV2(@RequestBody @Valid SignUpRequest request) {
        var user = authenticationService.signUpV2(request.email(), request.password());
        return ResponseEntity.ok(user);
    }

    @PostMapping("/validate")
    public ResponseEntity<SessionStatusResponse> validateToken(@RequestBody @Valid ValidateTokenRequest request) {
        var sessionStatus = authenticationService.validate(request.token(), request.email());
        return ResponseEntity.ok(new SessionStatusResponse(sessionStatus.name()));
    }

}
