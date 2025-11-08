package com.example.Quiz_project.controller;

import com.example.Quiz_project.entity.OptionChoice;
import com.example.Quiz_project.entity.Question;
import com.example.Quiz_project.entity.Quiz;
import com.example.Quiz_project.service.QuizService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class QuizAdminControllerTest {

    @Mock
    private QuizService quizService;

    @InjectMocks
    private QuizAdminController quizAdminController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    // ✅ Create quiz
    @Test
    void testCreateQuiz() {
        Quiz quiz = new Quiz();
        quiz.setId(1L);

        when(quizService.createQuiz(quiz)).thenReturn(quiz);

        Quiz result = quizAdminController.createQuiz(quiz);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        verify(quizService, times(1)).createQuiz(quiz);
    }

    // ✅ Get all quizzes
    @Test
    void testGetAll() {
        when(quizService.getAll()).thenReturn(List.of(new Quiz(), new Quiz()));

        List<Quiz> result = quizAdminController.getAll();

        assertEquals(2, result.size());
        verify(quizService, times(1)).getAll();
    }

    // ✅ Get quiz by id
    @Test
    void testGetQuiz() {
        Quiz quiz = new Quiz();
        quiz.setId(5L);

        when(quizService.getQuiz(5L)).thenReturn(quiz);

        Quiz result = quizAdminController.getQuiz(5L);

        assertNotNull(result);
        assertEquals(5L, result.getId());
        verify(quizService, times(1)).getQuiz(5L);
    }

    // ✅ Update quiz
    @Test
    void testUpdateQuiz() {
        Quiz quiz = new Quiz();
        quiz.setId(10L);

        when(quizService.updateQuiz(10L, quiz)).thenReturn(quiz);

        Quiz result = quizAdminController.updateQuiz(10L, quiz);

        assertNotNull(result);
        assertEquals(10L, result.getId());
        verify(quizService, times(1)).updateQuiz(10L, quiz);
    }

    // ✅ Delete quiz
    @Test
    void testDeleteQuiz() {
        quizAdminController.deleteQuiz(3L);
        verify(quizService, times(1)).deleteQuiz(3L);
    }

    // ✅ Add Question
    @Test
    void testAddQuestion() {
        Question q = new Question();
        q.setId(20L);

        when(quizService.addQuestion(1L, q)).thenReturn(q);

        Question result = quizAdminController.addQuestion(1L, q);

        assertNotNull(result);
        assertEquals(20L, result.getId());
        verify(quizService, times(1)).addQuestion(1L, q);
    }

    // ✅ Update Question
    @Test
    void testUpdateQuestion() {
        Question q = new Question();
        q.setId(100L);

        when(quizService.updateQuestion(100L, q)).thenReturn(q);

        Question result = quizAdminController.updateQuestion(100L, q);

        assertEquals(100L, result.getId());
        verify(quizService, times(1)).updateQuestion(100L, q);
    }

    // ✅ Delete Question
    @Test
    void testDeleteQuestion() {
        quizAdminController.deleteQuestion(200L);
        verify(quizService, times(1)).deleteQuestion(200L);
    }

    // ✅ Add Option
    @Test
    void testAddOption() {
        OptionChoice opt = new OptionChoice();
        opt.setId(300L);

        when(quizService.addOption(5L, opt)).thenReturn(opt);

        OptionChoice result = quizAdminController.addOption(5L, opt);

        assertNotNull(result);
        assertEquals(300L, result.getId());
        verify(quizService, times(1)).addOption(5L, opt);
    }

    // ✅ Delete Option
    @Test
    void testDeleteOption() {
        quizAdminController.deleteOption(400L);

        verify(quizService, times(1)).deleteOption(400L);
    }
}

