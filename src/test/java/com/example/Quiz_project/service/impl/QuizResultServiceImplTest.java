package com.example.Quiz_project.service.impl;

import com.example.Quiz_project.entity.*;
import com.example.Quiz_project.repository.QuizRepository;
import com.example.Quiz_project.repository.QuizResultRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class QuizResultServiceImplTest {

    @Mock
    private QuizRepository quizRepository;

    @Mock
    private QuizResultRepository quizResultRepository;

    @InjectMocks
    private QuizResultServiceImpl quizResultService;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    // ✅ TEST evaluateQuiz
    @Test
    void testEvaluateQuiz() {
        // Question & Options
        OptionChoice opt1 = new OptionChoice();
        opt1.setId(100L);
        opt1.setCorrect(true);

        OptionChoice opt2 = new OptionChoice();
        opt2.setId(200L);
        opt2.setCorrect(false);

        Question q = new Question();
        q.setId(1L);
        q.setOptions(List.of(opt1, opt2));

        Quiz quiz = new Quiz();
        quiz.setId(10L);
        quiz.setQuestions(List.of(q));

        when(quizRepository.fetchFullQuiz(10L)).thenReturn(Optional.of(quiz));

        Map<Long, Long> answers = Map.of(1L, 100L);

        int score = quizResultService.evaluateQuiz(10L, answers);

        assertEquals(1, score);
    }

    // ❌ evaluateQuiz: quiz not found
    @Test
    void testEvaluateQuizNotFound() {
        when(quizRepository.fetchFullQuiz(99L)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> quizResultService.evaluateQuiz(99L, Map.of()));

        assertEquals("Quiz Not Found", ex.getMessage());
    }

    // ✅ saveAttempt
    @Test
    void testSaveAttempt() {
        // Question
        OptionChoice correctOpt = new OptionChoice();
        correctOpt.setId(100L);
        correctOpt.setCorrect(true);

        Question q = new Question();
        q.setId(1L);
        q.setOptions(List.of(correctOpt));

        Quiz quiz = new Quiz();
        quiz.setId(10L);
        quiz.setQuestions(List.of(q));

        Map<Long, Long> answers = Map.of(1L, 100L);

        // Mock DB fetch
        when(quizRepository.fetchFullQuiz(10L)).thenReturn(Optional.of(quiz));

        QuizResult saved = QuizResult.builder()
                .id(5L)
                .userName("john")
                .quiz(quiz)
                .score(1)
                .totalQuestions(1)
                .chosenAnswers("{1=100}")
                .submittedAt(LocalDateTime.now())
                .build();

        when(quizResultRepository.save(any())).thenReturn(saved);

        QuizResult result = quizResultService.saveAttempt(10L, "john", answers);

        assertNotNull(result);
        assertEquals(1, result.getScore());
        assertEquals(1, result.getTotalQuestions());
        assertEquals("john", result.getUserName());

        verify(quizResultRepository, times(1)).save(any());
    }

    // ✅ saveAttempt should store "Guest" if userName null
    @Test
    void testSaveAttemptGuest() {
        Quiz quiz = new Quiz();
        quiz.setId(10L);
        quiz.setQuestions(List.of(new Question()));

        when(quizRepository.fetchFullQuiz(10L)).thenReturn(Optional.of(quiz));

        QuizResult saved = QuizResult.builder()
                .userName("Guest")
                .quiz(quiz)
                .score(0)
                .totalQuestions(1)
                .submittedAt(LocalDateTime.now())
                .build();

        when(quizResultRepository.save(any())).thenReturn(saved);

        QuizResult result = quizResultService.saveAttempt(10L, null, Map.of());

        assertEquals("Guest", result.getUserName());
    }

    // ❌ saveAttempt: quiz not found
    @Test
    void testSaveAttemptQuizNotFound() {
        when(quizRepository.fetchFullQuiz(50L)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> quizResultService.saveAttempt(50L, "user", Map.of()));

        assertEquals("Quiz Not Found", ex.getMessage());
    }

    // ✅ getAllResults
    @Test
    void testGetAllResults() {
        when(quizResultRepository.findAllByOrderBySubmittedAtDesc())
                .thenReturn(List.of(new QuizResult(), new QuizResult()));

        List<QuizResult> results = quizResultService.getAllResults();

        assertEquals(2, results.size());
        verify(quizResultRepository, times(1)).findAllByOrderBySubmittedAtDesc();
    }

    // ✅ getResultsByUser
    @Test
    void testGetResultsByUser() {
        when(quizResultRepository.findByUserNameOrderBySubmittedAtDesc("john"))
                .thenReturn(List.of(new QuizResult()));

        List<QuizResult> results = quizResultService.getResultsByUser("john");

        assertEquals(1, results.size());
        verify(quizResultRepository, times(1))
                .findByUserNameOrderBySubmittedAtDesc("john");
    }
}

