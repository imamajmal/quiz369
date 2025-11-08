package com.example.Quiz_project.repository;

import com.example.Quiz_project.entity.EmailTemplate;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class EmailTemplateRepositoryTest {

    @Autowired
    private EmailTemplateRepository templateRepository;

    @Test
    void testSaveAndFindByCode() {
        EmailTemplate tpl = new EmailTemplate();
        tpl.setCode("REG_CONFIRM");
        tpl.setSubject("Hello {{name}}");
        tpl.setBody("Welcome, {{name}}!");

        templateRepository.save(tpl);

        Optional<EmailTemplate> found = templateRepository.findByCode("REG_CONFIRM");

        assertTrue(found.isPresent());
        assertEquals("Hello {{name}}", found.get().getSubject());
        assertEquals("Welcome, {{name}}!", found.get().getBody());
    }

    @Test
    void testFindByCode_NotFound() {
        Optional<EmailTemplate> result = templateRepository.findByCode("UNKNOWN");
        assertTrue(result.isEmpty());
    }
}

