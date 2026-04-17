package com.project.guviproject2.controller;

import java.security.Principal;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.project.guviproject2.dto.admin.AdminUserResponse;
import com.project.guviproject2.dto.admin.UpdateUserRoleRequest;
import com.project.guviproject2.service.AdminUserService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/admin/users")
public class AdminUserController {

    private final AdminUserService adminUserService;

    public AdminUserController(AdminUserService adminUserService) {
        this.adminUserService = adminUserService;
    }

    @GetMapping
    public ResponseEntity<List<AdminUserResponse>> all() {
        return ResponseEntity.ok(adminUserService.listUsers());
    }

    @PutMapping("/{userId}/role")
    public ResponseEntity<AdminUserResponse> updateRole(@PathVariable Long userId,
                                                        @Valid @RequestBody UpdateUserRoleRequest request) {
        return ResponseEntity.ok(adminUserService.updateRole(userId, request.getRole()));
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> delete(@PathVariable Long userId, Principal principal) {
        adminUserService.deleteUser(userId, principal);
        return ResponseEntity.noContent().build();
    }
}
