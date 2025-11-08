package com.example.Quiz_project.repository;

import com.example.Quiz_project.entity.Role;
import com.example.Quiz_project.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest   // ✅ Loads only JPA + H2
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    void testFindByUsername() {
        // ✅ given: a user saved in database
        User u = new User();
        u.setUsername("imam");
        u.setPassword("1234");
        u.setRole(Role.ADMIN);
        userRepository.save(u);

        // ✅ when: search by username
        Optional<User> result = userRepository.findByUsername("imam");

        // ✅ then:
        assertTrue(result.isPresent());
        assertEquals("imam", result.get().getUsername());
    }

    @Test
    void testFindByUsername_NotFound() {
        Optional<User> result = userRepository.findByUsername("unknown");
        assertTrue(result.isEmpty());
    }
}

