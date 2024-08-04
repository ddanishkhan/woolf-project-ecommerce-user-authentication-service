package com.ecommerce_user_authentication.controller;

import com.ecommerce_user_authentication.dto.UserDto;
import com.ecommerce_user_authentication.dto.request.SetUserRolesRequest;
import com.ecommerce_user_authentication.service.UserService;
import org.springframework.http.ResponseEntity;
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

    @GetMapping("/{id}")
    public ResponseEntity<UserDto> getUserDetails(@PathVariable("id") Long userId) {
        UserDto userDto = userService.getUserDetails(userId);
        return ResponseEntity.ok(userDto);
    }

    @PostMapping("/{id}/roles")
    public ResponseEntity<UserDto> setUserRoles(@PathVariable("id") Long userId, @RequestBody SetUserRolesRequest request) {
        UserDto userDto = userService.setUserRoles(userId, request.roleIds());
        return ResponseEntity.ok(userDto);
    }

}
