package com.example.Quiz_project.controller;

import com.example.Quiz_project.entity.User;
import com.example.Quiz_project.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AuthControllerTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private AuthController authController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    // ✅ Test POST /auth/register
    @Test
    void testRegister() {
        User user = new User();
        user.setUsername("imam");
        user.setPassword("1234");

        when(userService.register(user)).thenReturn(user);

        User result = authController.register(user);

        assertNotNull(result);
        assertEquals("imam", result.getUsername());
        verify(userService, times(1)).register(user);
    }

    // ✅ Test POST /auth/login
    @Test
    void testLogin() {
        User user = new User();
        user.setUsername("imam");
        user.setPassword("1234");

        when(userService.login("imam", "1234")).thenReturn("TOKEN123");

        String token = authController.login(user);

        assertEquals("TOKEN123", token);
        verify(userService, times(1)).login("imam", "1234");
    }
}

