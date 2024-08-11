package com.ecommerce_user_authentication.controller;

import com.ecommerce_user_authentication.dto.UserDto;
import com.ecommerce_user_authentication.dto.request.SetUserRolesRequest;
import com.ecommerce_user_authentication.model.UserEntity;
import com.ecommerce_user_authentication.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/me")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<UserEntity> authenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserEntity currentUser = null;
        if (authentication.getPrincipal() instanceof UserEntity user){
            return ResponseEntity.ok(user);
        }
        System.out.println("ERROR: security context did not find any user instance.");
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<UserDto> getUserDetails(@PathVariable("id") Long userId) {
        var userDto = userService.getUserDetails(userId);
        return userDto.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/email/{email}")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<UserDto> getUserDetailsByEmail(@PathVariable String email) {
        var userDto = userService.getUserDetailsByUserEmail(email);
        return userDto.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping("/{id}/role")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<UserDto> setUserRole(@PathVariable("id") Long userId, @RequestBody @Valid SetUserRolesRequest request) {
        var userDto = userService.setUserRole(userId, request.role());
        return ResponseEntity.ok(userDto);
    }

}
