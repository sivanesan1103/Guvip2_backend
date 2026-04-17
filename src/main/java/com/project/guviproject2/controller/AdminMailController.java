package com.project.guviproject2.controller;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.project.guviproject2.dto.admin.TestMailRequest;
import com.project.guviproject2.service.EmailService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/admin/mail")
public class AdminMailController {

    private final EmailService emailService;

    public AdminMailController(EmailService emailService) {
        this.emailService = emailService;
    }

    @PostMapping("/test")
    public ResponseEntity<Map<String, String>> sendTest(@Valid @RequestBody TestMailRequest request) {
        emailService.sendTestEmail(request.getTo(), request.getSubject(), request.getBody());
        return ResponseEntity.ok(Map.of("message", "Test email sent successfully"));
    }
}
