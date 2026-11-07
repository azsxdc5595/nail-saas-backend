package com.nailsaas.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nailsaas.domain.GetUserRolesResponse;
import com.nailsaas.service.UserRoleService;

@RestController
@RequestMapping("/user/me/roles")
public class UserRoleController {

    private final UserRoleService userRoleService;

    public UserRoleController(UserRoleService userRoleService) {
        this.userRoleService = userRoleService;
    }

    @GetMapping
    public GetUserRolesResponse getCurrentUserRoles() {
        return userRoleService.getCurrentUserRoles();
    }
}
