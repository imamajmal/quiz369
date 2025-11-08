package com.example.Quiz_project.controller;

import com.example.Quiz_project.dto.*;
import com.example.Quiz_project.service.EmailService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class EmailControllerTest {

    @Mock
    private EmailService emailService;

    @InjectMocks
    private EmailController emailController;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    // ✅ /notifications/send
    @Test
    void testSendGeneric() {
        SendEmailRequest req = new SendEmailRequest();
        req.setTo("test@mail.com");
        req.setSubject("Test");
        req.setTemplateCode("REGISTRATION");

        emailController.sendGeneric(req);

        verify(emailService, times(1)).sendUsingTemplate(req);
    }

    // ✅ /notifications/registration
    @Test
    void testRegistration() {
        RegistrationEmailRequest req = new RegistrationEmailRequest();
        req.setEmail("user@mail.com");
        req.setName("John");

        emailController.registration(req);

        verify(emailService, times(1)).sendRegistrationEmail(req);
    }

    // ✅ /notifications/quiz-result
    @Test
    void testQuizResult() {
        QuizResultEmailRequest req = new QuizResultEmailRequest();
        req.setEmail("user@mail.com");
        req.setScore(90);

        emailController.quizResult(req);

        verify(emailService, times(1)).sendQuizResultEmail(req);
    }

    // ✅ /notifications/password-reset
    @Test
    void testPasswordReset() {
        PasswordResetEmailRequest req = new PasswordResetEmailRequest();
        req.setEmail("user@mail.com");

        emailController.passwordReset(req);

        verify(emailService, times(1)).sendPasswordResetEmail(req);
    }

    // ✅ /notifications/reset-password?token=xxx
    @Test
    void testVerifyReset() {
        String result = emailController.verifyReset("abc123");

        assertEquals("Reset Page Loaded for token: abc123", result);
    }
}
