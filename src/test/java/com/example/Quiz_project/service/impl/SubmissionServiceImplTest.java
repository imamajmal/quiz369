package com.example.Quiz_project.service.impl;

import com.example.Quiz_project.dto.*;
import com.example.Quiz_project.entity.*;
import com.example.Quiz_project.repository.*;
import com.example.Quiz_project.service.SubmissionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class SubmissionServiceImplTest {

    @Mock
    private QuizRepository quizRepo;

    @Mock
    private QuestionRepository questionRepo;

    @Mock
    private OptionRepository optionRepo;

    @Mock
    private AttemptRepository attemptRepo;

    @Mock
    private AttemptAnswerRepository attemptAnswerRepo;

    @InjectMocks
    private SubmissionServiceImpl submissionService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    // ✅ Test startAttempt()
    @Test
    void testStartAttempt() {
        Quiz quiz = new Quiz();
        quiz.setId(1L);
        quiz.setTimeLimit(10);
        quiz.setQuestions(List.of(new Question()));
        
        Attempt savedAttempt = Attempt.builder()
                .id(5L)
                .quiz(quiz)
                .username("user")
                .startedAt(Instant.now())
                .timeLimitMinutes(10)
                .totalQuestions(1)
                .build();

        when(quizRepo.findById(1L)).thenReturn(Optional.of(quiz));
        when(attemptRepo.save(any(Attempt.class))).thenReturn(savedAttempt);

        StartAttemptResponse response = submissionService.startAttempt(1L, "user");

        assertEquals(5L, response.attemptId());
        assertEquals(1L, response.quizId());
        verify(attemptRepo, times(1)).save(any(Attempt.class));
    }

    // ✅ Test submit(): Success
    @Test
    void testSubmitSuccess() {
        Attempt attempt = new Attempt();
        attempt.setId(10L);
        attempt.setUsername("user");
        attempt.setStartedAt(Instant.now());
        attempt.setTimeLimitMinutes(60);
        attempt.setTotalQuestions(1);

        Question question = new Question();
        question.setId(100L);

        OptionChoice option = new OptionChoice();
        option.setId(200L);
        option.setCorrect(true);

        SubmitRequest.Answer ans = new SubmitRequest.Answer(100L, 200L);
        SubmitRequest req = new SubmitRequest(10L, List.of(ans));

        when(attemptRepo.findById(10L)).thenReturn(Optional.of(attempt));
        when(questionRepo.findById(100L)).thenReturn(Optional.of(question));
        when(optionRepo.findById(200L)).thenReturn(Optional.of(option));

        SubmitResult result = submissionService.submit(req, "user");

        assertEquals(10L, result.attemptId());
        assertEquals(1, result.correctCount());
        assertEquals(1, result.totalQuestions());
        verify(attemptAnswerRepo, times(1)).save(any(AttemptAnswer.class));
    }

    // ❌ wrong user submitting
    @Test
    void testSubmitInvalidUser() {
        Attempt attempt = new Attempt();
        attempt.setId(10L);
        attempt.setUsername("john");

        SubmitRequest req = new SubmitRequest(10L, List.of());

        when(attemptRepo.findById(10L)).thenReturn(Optional.of(attempt));

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> submissionService.submit(req, "user"));

        assertEquals("Attempt belongs to another user", ex.getMessage());
    }

    // ❌ submission after time expired
    @Test
    void testSubmitLate() {
        Attempt attempt = new Attempt();
        attempt.setId(11L);
        attempt.setUsername("user");
        attempt.setStartedAt(Instant.now().minusSeconds(3600)); // started 1 hr ago
        attempt.setTimeLimitMinutes(1);

        SubmitRequest req = new SubmitRequest(11L, List.of());

        when(attemptRepo.findById(11L)).thenReturn(Optional.of(attempt));

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> submissionService.submit(req, "user"));

        assertEquals("Time is over for this attempt", ex.getMessage());
    }

    // ❌ attempt not found
    @Test
    void testAttemptNotFound() {
        when(attemptRepo.findById(99L)).thenReturn(Optional.empty());

        SubmitRequest req = new SubmitRequest(99L, List.of());

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> submissionService.submit(req, "user"));

        assertEquals("Attempt not found", ex.getMessage());
    }

    // ✅ getAttempt success
    @Test
    void testGetAttemptSuccess() {
        Attempt attempt = new Attempt();
        attempt.setId(20L);

        when(attemptRepo.findById(20L)).thenReturn(Optional.of(attempt));

        Attempt result = submissionService.getAttempt(20L);

        assertEquals(20L, result.getId());
    }

    // ❌ getAttempt not found
    @Test
    void testGetAttemptNotFound() {
        when(attemptRepo.findById(999L)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> submissionService.getAttempt(999L));

        assertEquals("Attempt not found", ex.getMessage());
    }
}
