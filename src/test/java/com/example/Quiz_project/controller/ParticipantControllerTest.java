package com.example.Quiz_project.controller;

import com.example.Quiz_project.dto.*;
import com.example.Quiz_project.entity.Attempt;
import com.example.Quiz_project.service.ParticipantService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.security.core.Authentication;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ParticipantControllerTest {

    @Mock
    private ParticipantService participantService;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private ParticipantController participantController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        when(authentication.getName()).thenReturn("testUser");
    }

    // ✅ list quizzes
    @Test
    void testListQuizzes() {
        when(participantService.listQuizzes())
                .thenReturn(List.of(new QuizViewDto(), new QuizViewDto()));

        List<QuizViewDto> result = participantController.listQuizzes();

        assertEquals(2, result.size());
        verify(participantService, times(1)).listQuizzes();
    }

    // ✅ get quiz by id
    @Test
    void testGetQuiz() {
        QuizViewDto dto = new QuizViewDto();
        dto.setQuizId(10L);

        when(participantService.getQuizForTaking(10L)).thenReturn(dto);

        QuizViewDto result = participantController.getQuiz(10L);

        assertNotNull(result);
        assertEquals(10L, result.getQuizId());
        verify(participantService, times(1)).getQuizForTaking(10L);
    }

    // ✅ start attempt
    @Test
    void testStartAttempt() {
        StartAttemptResponse resp = new StartAttemptResponse();
        resp.setAttemptId(5L);

        when(participantService.startAttempt(2L, "testUser"))
                .thenReturn(resp);

        StartAttemptResponse result = participantController.start(2L, authentication);

        assertNotNull(result);
        assertEquals(5L, result.getAttemptId());
        verify(participantService, times(1)).startAttempt(2L, "testUser");
    }

    // ✅ submit attempt
    @Test
    void testSubmit() {
        SubmitRequest req = new SubmitRequest();
        req.setAttemptId(99L);

        SubmitResult resultMock = new SubmitResult();
        resultMock.setScore(80);

        when(participantService.submitAttempt(req, "testUser"))
                .thenReturn(resultMock);

        SubmitResult result = participantController.submit(req, authentication);

        assertNotNull(result);
        assertEquals(80, result.getScore());
        verify(participantService, times(1)).submitAttempt(req, "testUser");
    }

    // ✅ get attempt summary
    @Test
    void testGetAttempt() {
        Attempt attempt = new Attempt();
        attempt.setId(40L);

        when(participantService.getAttempt(40L)).thenReturn(attempt);

        Attempt result = participantController.getAttempt(40L);

        assertEquals(40L, result.getId());
        verify(participantService, times(1)).getAttempt(40L);
    }
}

