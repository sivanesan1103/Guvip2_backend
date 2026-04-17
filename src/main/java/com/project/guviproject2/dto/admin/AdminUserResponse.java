package com.project.guviproject2.dto.admin;

import com.project.guviproject2.entity.Role;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AdminUserResponse {
    private Long id;
    private String email;
    private Role role;
}
