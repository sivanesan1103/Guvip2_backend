package com.project.guviproject2.security;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class JwtServiceTest {

    private JwtService jwtService;

    @BeforeEach
    void setUp() {
        jwtService = new JwtService("unit-test-secret", 60_000);
    }

    @Test
    void generateTokenAndExtractEmail() {
        String email = "user@example.com";

        String token = jwtService.generateToken(email);
        String extractedEmail = jwtService.extractEmail(token);

        assertEquals(email, extractedEmail);
    }

    @Test
    void extractEmailReturnsNullForInvalidToken() {
        assertNull(jwtService.extractEmail("not-a-jwt-token"));
    }
}
