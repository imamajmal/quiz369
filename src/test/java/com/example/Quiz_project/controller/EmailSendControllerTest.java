package com.example.Quiz_project.controller;

import com.example.Quiz_project.controller.EmailSendController.EmailRequest;
import com.example.Quiz_project.service.EmailService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class EmailSendControllerTest {

    @Mock
    private EmailService emailService;

    @InjectMocks
    private EmailSendController controller;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    // ✅ Test GET /email/test
    @Test
    void testMail() {
        String response = controller.testMail();

        assertEquals("✅ Test email sent!", response);

        verify(emailService, times(1))
                .sendMail(
                        "imamdeen60@gmail.com",
                        "Test Mail from Spring Boot",
                        "✅ Your Spring Boot email service is working successfully!"
                );
    }

    // ✅ Test POST /email/send
    @Test
    void testSendDynamicMail() {
        EmailRequest req = new EmailRequest();
        req.setTo("test@mail.com");
        req.setSubject("Hello");
        req.setMessage("This is a test");

        String response = controller.sendDynamicMail(req);

        assertEquals("✅ Email sent to: test@mail.com", response);

        verify(emailService, times(1))
                .sendMail("test@mail.com", "Hello", "This is a test");
    }
}

