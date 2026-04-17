package com.project.guviproject2.dto.auth;

import com.project.guviproject2.entity.Role;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AuthResponse {
    private String token;
    private Long userId;
    private String email;
    private Role role;
}
