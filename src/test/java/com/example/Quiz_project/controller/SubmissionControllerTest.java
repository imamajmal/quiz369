package com.example.Quiz_project.controller;

import com.example.Quiz_project.dto.*;
import com.example.Quiz_project.entity.Attempt;
import com.example.Quiz_project.service.SubmissionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.security.core.Authentication;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class SubmissionControllerTest {

    @Mock
    private SubmissionService submissionService;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private SubmissionController submissionController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        when(authentication.getName()).thenReturn("testUser");
    }

    @Test
    void testStartAttempt() {
        Long quizId = 10L;

        StartAttemptResponse mockResponse = new StartAttemptResponse();
        mockResponse.setAttemptId(1L);

        when(submissionService.startAttempt(quizId, "testUser"))
                .thenReturn(mockResponse);

        StartAttemptResponse response =
                submissionController.start(quizId, authentication);

        assertNotNull(response);
        assertEquals(1L, response.getAttemptId());
        verify(submissionService, times(1)).startAttempt(quizId, "testUser");
    }

    @Test
    void testSubmitAnswers() {
        SubmitRequest req = new SubmitRequest();
        req.setAttemptId(1L);

        SubmitResult mockResult = new SubmitResult();
        mockResult.setScore(90);

        when(submissionService.submit(req, "testUser"))
                .thenReturn(mockResult);

        SubmitResult result =
                submissionController.submit(req, authentication);

        assertNotNull(result);
        assertEquals(90, result.getScore());
        verify(submissionService, times(1)).submit(req, "testUser");
    }

    @Test
    void testGetAttempt() {
        Long attemptId = 5L;

        Attempt mockAttempt = new Attempt();
        mockAttempt.setId(attemptId);

        when(submissionService.getAttempt(attemptId))
                .thenReturn(mockAttempt);

        Attempt result =
                submissionController.getAttempt(attemptId);

        assertNotNull(result);
        assertEquals(attemptId, result.getId());
        verify(submissionService, times(1)).getAttempt(attemptId);
    }
}
