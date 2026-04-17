package com.project.guviproject2.dto.admin;

import com.project.guviproject2.entity.Role;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UpdateUserRoleRequest {
    @NotNull
    private Role role;
}
