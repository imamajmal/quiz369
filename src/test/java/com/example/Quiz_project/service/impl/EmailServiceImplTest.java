package com.example.Quiz_project.service.impl;

import com.example.Quiz_project.dto.*;
import com.example.Quiz_project.entity.EmailLog;
import com.example.Quiz_project.entity.EmailTemplate;
import com.example.Quiz_project.repository.EmailLogRepository;
import com.example.Quiz_project.repository.EmailTemplateRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import jakarta.mail.internet.MimeMessage;

import java.time.Instant;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class EmailServiceImplTest {

    @Mock private JavaMailSender mailSender;
    @Mock private EmailTemplateRepository templateRepo;
    @Mock private EmailLogRepository logRepo;
    @Mock private SpringTemplateEngine templateEngine;

    @InjectMocks
    private EmailServiceImpl service;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    // ✅ Test sendUsingTemplate()
    @Test
    void testSendUsingTemplate() {
        EmailTemplate tpl = new EmailTemplate();
        tpl.setCode("REG_CONFIRM");
        tpl.setSubject("Hello {{name}}");
        tpl.setBody("Your account is ready, {{name}}!");

        when(templateRepo.findByCode("REG_CONFIRM"))
                .thenReturn(Optional.of(tpl));

        // Mock mail sending OK
        doNothing().when(mailSender).send(any(SimpleMailMessage.class));

        SendEmailRequest req = new SendEmailRequest(
                "test@mail.com",
                "REG_CONFIRM",
                Map.of("name", "John")
        );

        service.sendUsingTemplate(req);

        verify(mailSender, times(1)).send(any(SimpleMailMessage.class));
        verify(logRepo, times(1)).save(any(EmailLog.class));
    }

    // ✅ Test sendUsingTemplate - missing template
    @Test
    void testSendUsingTemplate_TemplateNotFound() {
        when(templateRepo.findByCode("INVALID"))
                .thenReturn(Optional.empty());

        SendEmailRequest req = new SendEmailRequest(
                "test@mail.com",
                "INVALID",
                Map.of()
        );

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> service.sendUsingTemplate(req));

        assertEquals("Template not found: INVALID", ex.getMessage());
    }

    // ✅ Test simple sendMail()
    @Test
    void testSendMail() {
        doNothing().when(mailSender).send(any(SimpleMailMessage.class));

        service.sendMail("user@mail.com", "Hello", "Test body");

        verify(mailSender, times(1)).send(any(SimpleMailMessage.class));
    }

    // ✅ Test sendEmail() with template (HTML email)
    @Test
    void testSendEmail_TemplateHtml() throws Exception {
        MimeMessage mimeMessage = mock(MimeMessage.class);
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);

        when(templateEngine.process(eq("template1"), any(Context.class)))
                .thenReturn("<h1>Hello</h1>");

        doNothing().when(mailSender).send(any(MimeMessage.class));

        service.sendEmail("user@mail.com", "Subject", "template1", Map.of("x", "y"));

        verify(mailSender, times(1)).send(mimeMessage);
        verify(logRepo, times(1)).save(any(EmailLog.class));
    }

    // ✅ Test sendUsingTemplate failed send() logs error
    @Test
    void testSendUsingTemplate_FailedMail() {
        EmailTemplate tpl = new EmailTemplate();
        tpl.setCode("ALERT");
        tpl.setSubject("Alert {{name}}");
        tpl.setBody("Warning {{name}}!");

        when(templateRepo.findByCode("ALERT"))
                .thenReturn(Optional.of(tpl));

        // Simulate mail failure
        doThrow(new RuntimeException("SMTP error"))
                .when(mailSender).send(any(SimpleMailMessage.class));

        SendEmailRequest req = new SendEmailRequest(
                "fail@mail.com",
                "ALERT",
                Map.of("name", "John")
        );

        assertThrows(RuntimeException.class,
                () -> service.sendUsingTemplate(req));

        verify(logRepo, times(1)).save(any(EmailLog.class)); // logged error
    }

    // ✅ Test sendEmail() HTML version logs error if failure occurs
    @Test
    void testSendEmail_Failure() throws Exception {
        MimeMessage mimeMessage = mock(MimeMessage.class);
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
        when(templateEngine.process(eq("TEMP"), any(Context.class)))
                .thenReturn("<p>Hi</p>");

        // force failure
        doThrow(new RuntimeException("SMTP ERROR"))
                .when(mailSender).send(any(MimeMessage.class));

        service.sendEmail("mail@test.com", "Subject", "TEMP", Map.of());

        verify(logRepo, times(1)).save(any(EmailLog.class));
    }
}

