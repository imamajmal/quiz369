package com.example.Quiz_project.service;

import com.example.Quiz_project.entity.Role;
import com.example.Quiz_project.entity.User;
import com.example.Quiz_project.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CustomUserDetailsServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private CustomUserDetailsService customUserDetailsService;

    @BeforeEach
    void init() {
        MockitoAnnotations.openMocks(this);
    }

    // ✅ Success case
    @Test
    void testLoadUserByUsername_Success() {
        User user = new User();
        user.setUsername("imam");
        user.setPassword("ENC_PASS");
        user.setRole(Role.ADMIN);

        when(userRepository.findByUsername("imam"))
                .thenReturn(Optional.of(user));

        UserDetails details = customUserDetailsService.loadUserByUsername("imam");

        assertNotNull(details);
        assertEquals("imam", details.getUsername());
        assertEquals("ENC_PASS", details.getPassword());
        assertTrue(details.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN")));
    }

    // ❌ User not found
    @Test
    void testLoadUserByUsername_NotFound() {
        when(userRepository.findByUsername("unknown"))
                .thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class,
                () -> customUserDetailsService.loadUserByUsername("unknown"));
    }
}
