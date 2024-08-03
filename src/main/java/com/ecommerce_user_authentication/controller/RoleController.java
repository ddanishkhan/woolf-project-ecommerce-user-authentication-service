package com.ecommerce_user_authentication.controller;

import com.ecommerce_user_authentication.dto.request.CreateRoleRequest;
import com.ecommerce_user_authentication.dto.response.RoleCreatedResponse;
import com.ecommerce_user_authentication.service.RoleService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/roles")
public class RoleController {

    private final RoleService roleService;

    public RoleController(RoleService roleService) {
        this.roleService = roleService;
    }

    @PostMapping
    public ResponseEntity<RoleCreatedResponse> createRole(@RequestBody CreateRoleRequest request) {
        RoleCreatedResponse role = roleService.createRole(request.name());
        return new ResponseEntity<>(role, HttpStatus.OK);
    }
}
