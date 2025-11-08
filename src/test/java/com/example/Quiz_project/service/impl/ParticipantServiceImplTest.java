package com.example.Quiz_project.service.impl;

import com.example.Quiz_project.dto.*;
import com.example.Quiz_project.entity.*;
import com.example.Quiz_project.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.time.Duration;
import java.time.Instant;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ParticipantServiceImplTest {

    @Mock private QuizRepository quizRepo;
    @Mock private QuestionRepository questionRepo;
    @Mock private OptionRepository optionRepo;
    @Mock private AttemptRepository attemptRepo;
    @Mock private AttemptAnswerRepository attemptAnswerRepo;

    @InjectMocks
    private ParticipantServiceImpl service;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    // ✅ listQuizzes()
    @Test
    void testListQuizzes() {
        Quiz q = new Quiz();
        q.setId(1L);
        q.setTitle("Java");
        q.setDescription("Basic");
        q.setTimeLimit(10);
        q.setQuestions(List.of(new Question()));

        when(quizRepo.findAll()).thenReturn(List.of(q));

        List<QuizViewDto> result = service.listQuizzes();

        assertEquals(1, result.size());
        assertEquals(1L, result.get(0).getQuizId());
        assertEquals("Java", result.get(0).getTitle());
    }

    // ✅ getQuizForTaking()
    @Test
    void testGetQuizForTaking() {
        Quiz quiz = new Quiz();
        quiz.setId(5L);
        quiz.setQuestions(List.of(new Question()));

        when(quizRepo.findById(5L)).thenReturn(Optional.of(quiz));

        QuizViewDto result = service.getQuizForTaking(5L);

        assertEquals(5L, result.getQuizId());
    }

    // ❌ getQuizForTaking not found
    @Test
    void testGetQuizForTakingNotFound() {
        when(quizRepo.findById(99L)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> service.getQuizForTaking(99L));

        assertEquals("Quiz not found", ex.getMessage());
    }

    // ✅ startAttempt()
    @Test
    void testStartAttempt() {
        Quiz quiz = new Quiz();
        quiz.setId(10L);
        quiz.setTimeLimit(15);
        quiz.setQuestions(List.of(new Question()));

        Attempt saved = Attempt.builder()
                .id(100L)
                .quiz(quiz)
                .username("user")
                .startedAt(Instant.now())
                .timeLimitMinutes(15)
                .totalQuestions(1)
                .build();

        when(quizRepo.findById(10L)).thenReturn(Optional.of(quiz));
        when(attemptRepo.save(any())).thenReturn(saved);

        StartAttemptResponse resp = service.startAttempt(10L, "user");

        assertEquals(100L, resp.attemptId());
        assertEquals(10L, resp.quizId());
        verify(attemptRepo).save(any());
    }

    // ✅ submitAttempt success
    @Test
    void testSubmitAttemptSuccess() {
        Attempt at = new Attempt();
        at.setId(7L);
        at.setUsername("user");
        at.setStartedAt(Instant.now());
        at.setTimeLimitMinutes(60);
        at.setTotalQuestions(1);

        Question q = new Question();
        q.setId(20L);

        OptionChoice opt = new OptionChoice();
        opt.setId(30L);
        opt.setCorrect(true);

        SubmitRequest.Answer ans = new SubmitRequest.Answer(20L, 30L);
        SubmitRequest req = new SubmitRequest(7L, List.of(ans));

        when(attemptRepo.findById(7L)).thenReturn(Optional.of(at));
        when(questionRepo.findById(20L)).thenReturn(Optional.of(q));
        when(optionRepo.findById(30L)).thenReturn(Optional.of(opt));

        SubmitResult result = service.submitAttempt(req, "user");

        assertEquals(7L, result.attemptId());
        assertEquals(1, result.correctCount());
        verify(attemptAnswerRepo).save(any());
        verify(attemptRepo).save(at);
    }

    // ❌ submitAttempt wrong user
    @Test
    void testSubmitAttemptWrongUser() {
        Attempt at = new Attempt();
        at.setId(8L);
        at.setUsername("admin");

        SubmitRequest req = new SubmitRequest(8L, List.of());

        when(attemptRepo.findById(8L)).thenReturn(Optional.of(at));

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> service.submitAttempt(req, "user"));

        assertEquals("Attempt belongs to another user", ex.getMessage());
    }

    // ❌ submitAttempt after time expired
    @Test
    void testSubmitAttemptLate() {
        Attempt at = new Attempt();
        at.setId(9L);
        at.setUsername("user");
        at.setStartedAt(Instant.now().minus(Duration.ofMinutes(30)));
        at.setTimeLimitMinutes(1);

        SubmitRequest req = new SubmitRequest(9L, List.of());

        when(attemptRepo.findById(9L)).thenReturn(Optional.of(at));

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> service.submitAttempt(req, "user"));

        assertEquals("Time is over for this attempt", ex.getMessage());
    }

    // ❌ submitAttempt attempt not found
    @Test
    void testSubmitAttemptAttemptNotFound() {
        SubmitRequest req = new SubmitRequest(100L, List.of());

        when(attemptRepo.findById(100L)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> service.submitAttempt(req, "user"));

        assertEquals("Attempt not found", ex.getMessage());
    }

    // ✅ getAttempt()
    @Test
    void testGetAttemptSuccess() {
        Attempt at = new Attempt();
        at.setId(55L);

        when(attemptRepo.findById(55L)).thenReturn(Optional.of(at));

        Attempt result = service.getAttempt(55L);

        assertEquals(55L, result.getId());
    }

    // ❌ getAttempt not found
    @Test
    void testGetAttemptNotFound() {
        when(attemptRepo.findById(123L)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> service.getAttempt(123L));

        assertEquals("Attempt not found", ex.getMessage());
    }
}
