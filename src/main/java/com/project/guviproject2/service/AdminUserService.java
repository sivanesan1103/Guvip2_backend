package com.project.guviproject2.service;

import java.security.Principal;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.project.guviproject2.dto.admin.AdminUserResponse;
import com.project.guviproject2.entity.Role;
import com.project.guviproject2.entity.User;
import com.project.guviproject2.exception.BadRequestException;
import com.project.guviproject2.exception.ResourceNotFoundException;
import com.project.guviproject2.repository.UserRepository;

@Service
public class AdminUserService {

    private final UserRepository userRepository;

    public AdminUserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<AdminUserResponse> listUsers() {
        return userRepository.findAll().stream()
                .map(user -> new AdminUserResponse(user.getId(), user.getEmail(), user.getRole()))
                .toList();
    }

    @Transactional
    public AdminUserResponse updateRole(Long userId, Role role) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        user.setRole(role);
        User saved = userRepository.save(user);
        return new AdminUserResponse(saved.getId(), saved.getEmail(), saved.getRole());
    }

    @Transactional
    public void deleteUser(Long userId, Principal principal) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        if (user.getEmail().equalsIgnoreCase(principal.getName())) {
            throw new BadRequestException("Admin cannot delete self");
        }
        userRepository.delete(user);
    }
}
