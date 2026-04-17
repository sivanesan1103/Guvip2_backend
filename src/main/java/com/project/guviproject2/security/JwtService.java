package com.project.guviproject2.security;

import java.time.Instant;
import java.util.Date;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;

@Service
public class JwtService {

    private final Algorithm algorithm;
    private final long expirationMs;

    public JwtService(@Value("${app.jwt.secret}") String secret, @Value("${app.jwt.expiration-ms}") long expirationMs) {
        this.algorithm = Algorithm.HMAC256(secret);
        this.expirationMs = expirationMs;
    }

    public String generateToken(String email) {
        Instant now = Instant.now();
        return JWT.create()
                .withSubject(email)
                .withIssuedAt(Date.from(now))
                .withExpiresAt(Date.from(now.plusMillis(expirationMs)))
                .sign(algorithm);
    }

    public String extractEmail(String token) {
        try {
            return JWT.require(algorithm).build().verify(token).getSubject();
        } catch (JWTVerificationException ex) {
            return null;
        }
    }
}
