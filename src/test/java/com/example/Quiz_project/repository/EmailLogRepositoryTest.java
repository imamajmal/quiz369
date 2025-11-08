package com.example.Quiz_project.repository;

import com.example.Quiz_project.entity.EmailLog;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class EmailLogRepositoryTest {

    @Autowired
    private EmailLogRepository emailLogRepository;

    @Test
    void testSaveAndFindSorted() {
        EmailLog log1 = EmailLog.builder()
                .toAddress("a@mail.com")
                .subject("Test 1")
                .sentAt(Instant.now().minusSeconds(60))
                .success(true)
                .build();

        EmailLog log2 = EmailLog.builder()
                .toAddress("b@mail.com")
                .subject("Test 2")
                .sentAt(Instant.now())
                .success(false)
                .build();

        emailLogRepository.save(log1);
        emailLogRepository.save(log2);

        List<EmailLog> result = emailLogRepository.findAllByOrderBySentAtDesc();

        assertEquals(2, result.size());
        assertEquals("Test 2", result.get(0).getSubject()); // newest first
        assertTrue(result.get(0).getSentAt().isAfter(result.get(1).getSentAt()));
    }

    @Test
    void testFindEmpty() {
        List<EmailLog> result = emailLogRepository.findAllByOrderBySentAtDesc();
        assertTrue(result.isEmpty());
    }

    @Test
    void testSaveSingle() {
        EmailLog log = EmailLog.builder()
                .toAddress("test@mail.com")
                .subject("Hello")
                .body("Testing email logs")
                .sentAt(Instant.now())
                .success(true)
                .build();

        EmailLog saved = emailLogRepository.save(log);

        assertNotNull(saved.getId());
        assertEquals("test@mail.com", saved.getToAddress());
    }
}

