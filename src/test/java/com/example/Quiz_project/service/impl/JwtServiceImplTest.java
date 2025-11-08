package com.example.Quiz_project.service.impl;

import com.example.Quiz_project.service.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class JwtServiceImplTest {

    private JwtServiceImpl jwtService;

    @BeforeEach
    void setUp() {
        jwtService = new JwtServiceImpl(); // real implementation
    }

    @Test
    void testGenerateToken() {
        String token = jwtService.generateToken("imam", "ADMIN");

        assertNotNull(token);
        assertTrue(token.length() > 20);
    }

    @Test
    void testExtractUsername() {
        String token = jwtService.generateToken("user123", "ADMIN");

        String username = jwtService.extractUsername(token);

        assertEquals("user123", username);
    }

    @Test
    void testValidateToken_Valid() {
        String token = jwtService.generateToken("testuser", "ADMIN");

        boolean result = jwtService.validateToken(token);

        assertTrue(result);
    }

    @Test
    void testValidateToken_Invalid() {
        // corrupted token
        String invalid = "xxx.yyy.zzz";

        boolean result = jwtService.validateToken(invalid);

        assertFalse(result);
    }

    @Test
    void testExtractUsername_InvalidToken() {
        String invalid = "broken.token.here";

        assertThrows(Exception.class, () -> jwtService.extractUsername(invalid));
    }
}

