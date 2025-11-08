package com.example.Quiz_project.service.impl;

import com.example.Quiz_project.entity.OptionChoice;
import com.example.Quiz_project.entity.Question;
import com.example.Quiz_project.entity.Quiz;
import com.example.Quiz_project.repository.OptionRepository;
import com.example.Quiz_project.repository.QuestionRepository;
import com.example.Quiz_project.repository.QuizRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class QuizServiceImplTest {

    @Mock
    private QuizRepository quizRepository;

    @Mock
    private QuestionRepository questionRepository;

    @Mock
    private OptionRepository optionRepository;

    @InjectMocks
    private QuizServiceImpl quizService;

    @BeforeEach
    void init() {
        MockitoAnnotations.openMocks(this);
    }

    // ✅ createQuiz()
    @Test
    void testCreateQuiz() {
        Quiz quiz = new Quiz();
        quiz.setTitle("Java Quiz");

        Question q = new Question();
        OptionChoice o = new OptionChoice();

        q.setOptions(List.of(o));
        quiz.setQuestions(List.of(q));

        when(quizRepository.save(quiz)).thenReturn(quiz);

        Quiz saved = quizService.createQuiz(quiz);

        assertNotNull(saved);
        verify(quizRepository, times(1)).save(quiz);

        // verify relationships assigned
        assertEquals(quiz, q.getQuiz());
        assertEquals(q, o.getQuestion());
    }

    // ✅ getAll()
    @Test
    void testGetAll() {
        when(quizRepository.findAll()).thenReturn(List.of(new Quiz(), new Quiz()));
        List<Quiz> list = quizService.getAll();
        assertEquals(2, list.size());
    }

    // ✅ getQuiz() success
    @Test
    void testGetQuizSuccess() {
        Quiz quiz = new Quiz();
        quiz.setId(1L);

        when(quizRepository.findById(1L)).thenReturn(Optional.of(quiz));

        Quiz result = quizService.getQuiz(1L);

        assertEquals(1L, result.getId());
    }

    // ❌ getQuiz() failure
    @Test
    void testGetQuizNotFound() {
        when(quizRepository.findById(99L)).thenReturn(Optional.empty());
        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> quizService.getQuiz(99L));
        assertEquals("Quiz not found with ID: 99", ex.getMessage());
    }

    // ✅ updateQuiz()
    @Test
    void testUpdateQuiz() {
        Quiz existing = new Quiz();
        existing.setId(1L);
        existing.setTitle("Old");

        Quiz updated = new Quiz();
        updated.setTitle("New");

        when(quizRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(quizRepository.save(existing)).thenReturn(existing);

        Quiz result = quizService.updateQuiz(1L, updated);

        assertEquals("New", result.getTitle());
    }

    // ✅ deleteQuiz()
    @Test
    void testDeleteQuiz() {
        quizService.deleteQuiz(5L);
        verify(quizRepository, times(1)).deleteById(5L);
    }

    // ✅ addQuestion()
    @Test
    void testAddQuestion() {
        Quiz quiz = new Quiz();
        quiz.setId(1L);

        Question q = new Question();

        when(quizRepository.findById(1L)).thenReturn(Optional.of(quiz));
        when(questionRepository.save(q)).thenReturn(q);

        Question result = quizService.addQuestion(1L, q);

        assertEquals(quiz, result.getQuiz());
        verify(questionRepository, times(1)).save(q);
    }

    // ✅ updateQuestion()
    @Test
    void testUpdateQuestion() {
        Question existing = new Question();
        existing.setId(10L);
        existing.setText("Old");

        Question newData = new Question();
        newData.setText("New");

        when(questionRepository.findById(10L)).thenReturn(Optional.of(existing));
        when(questionRepository.save(existing)).thenReturn(existing);

        Question result = quizService.updateQuestion(10L, newData);

        assertEquals("New", result.getText());
    }

    // ✅ addOption()
    @Test
    void testAddOption() {
        Question q = new Question();
        q.setId(4L);

        OptionChoice opt = new OptionChoice();

        when(questionRepository.findById(4L)).thenReturn(Optional.of(q));
        when(optionRepository.save(opt)).thenReturn(opt);

        OptionChoice result = quizService.addOption(4L, opt);

        assertEquals(q, result.getQuestion());
        verify(optionRepository, times(1)).save(opt);
    }

    // ✅ deleteOption()
    @Test
    void testDeleteOption() {
        quizService.deleteOption(200L);
        verify(optionRepository, times(1)).deleteById(200L);
    }

    // ✅ totalQuestions()
    @Test
    void testTotalQuestions() {
        Question q1 = new Question();
        Question q2 = new Question();

        Quiz quiz = new Quiz();
        quiz.setQuestions(List.of(q1, q2));

        when(quizRepository.fetchFullQuiz(1L)).thenReturn(Optional.of(quiz));

        int total = quizService.totalQuestions(1L);

        assertEquals(2, total);
    }

    // ✅ gradeQuiz()
    @Test
    void testGradeQuiz() {
        OptionChoice correct = new OptionChoice();
        correct.setId(100L);
        correct.setCorrect(true);

        OptionChoice wrong = new OptionChoice();
        wrong.setId(200L);
        wrong.setCorrect(false);

        Question q = new Question();
        q.setId(1L);
        q.setOptions(List.of(correct, wrong));

        Quiz quiz = new Quiz();
        quiz.setQuestions(List.of(q));

        Map<Long, Long> answers = Map.of(1L, 100L);

        when(quizRepository.fetchFullQuiz(1L)).thenReturn(Optional.of(quiz));

        int score = quizService.gradeQuiz(1L, answers);

        assertEquals(1, score);
    }
}

