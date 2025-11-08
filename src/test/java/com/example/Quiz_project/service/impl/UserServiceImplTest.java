package com.example.Quiz_project.service.impl;

import com.example.Quiz_project.entity.Role;
import com.example.Quiz_project.entity.User;
import com.example.Quiz_project.repository.UserRepository;
import com.example.Quiz_project.service.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private UserServiceImpl userService;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    // ✅ register()
    @Test
    void testRegister() {
        User user = new User();
        user.setUsername("imam");
        user.setPassword("1234");

        when(passwordEncoder.encode("1234")).thenReturn("ENCODED1234");
        when(userRepository.save(user)).thenReturn(user);

        User saved = userService.register(user);

        assertEquals("ENCODED1234", saved.getPassword());
        verify(passwordEncoder, times(1)).encode("1234");
        verify(userRepository, times(1)).save(user);
    }

    // ✅ login success -> returns token
    @Test
    void testLoginSuccess() {
        User user = new User();
        user.setUsername("imam");
        user.setPassword("ENCODED_PASS");
        user.setRole(Role.ADMIN);

        when(userRepository.findByUsername("imam"))
                .thenReturn(Optional.of(user));

        when(passwordEncoder.matches("1234", "ENCODED_PASS"))
                .thenReturn(true);

        when(jwtService.generateToken("imam", "ADMIN"))
                .thenReturn("JWT_TOKEN");

        String result = userService.login("imam", "1234");

        assertEquals("JWT_TOKEN", result);
        verify(jwtService, times(1))
                .generateToken("imam", "ADMIN");
    }

    // ✅ login - wrong password
    @Test
    void testLoginInvalidPassword() {
        User user = new User();
        user.setUsername("imam");
        user.setPassword("ENCODED_PASS");

        when(userRepository.findByUsername("imam"))
                .thenReturn(Optional.of(user));

        when(passwordEncoder.matches("wrong", "ENCODED_PASS"))
                .thenReturn(false);

        String result = userService.login("imam", "wrong");

        assertEquals("Invalid credentials", result);
    }

    // ✅ login - user not found
    @Test
    void testLoginUserNotFound() {
        when(userRepository.findByUsername("unknown"))
                .thenReturn(Optional.empty());

        String result = userService.login("unknown", "1234");

        assertEquals("User not found", result);
    }

    // ✅ getByUsername success
    @Test
    void testGetByUsernameSuccess() {
        User user = new User();
        user.setUsername("imam");

        when(userRepository.findByUsername("imam"))
                .thenReturn(Optional.of(user));

        User result = userService.getByUsername("imam");

        assertEquals("imam", result.getUsername());
    }

    // ✅ getByUsername throws exception
    @Test
    void testGetByUsernameNotFound() {

        when(userRepository.findByUsername("abc"))
                .thenReturn(Optional.empty());

        RuntimeException e = assertThrows(RuntimeException.class,
                () -> userService.getByUsername("abc"));

        assertEquals("User not found", e.getMessage());
    }
}

