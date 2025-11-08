package com.example.Quiz_project.repository;

import com.example.Quiz_project.entity.OptionChoice;
import com.example.Quiz_project.entity.Question;
import com.example.Quiz_project.entity.Quiz;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class QuizRepositoryTest {

    @Autowired
    private QuizRepository quizRepository;

    @Test
    void testFindAllByOrderByIdAsc() {
        Quiz q1 = new Quiz();
        q1.setTitle("A Quiz");
        quizRepository.save(q1);

        Quiz q2 = new Quiz();
        q2.setTitle("B Quiz");
        quizRepository.save(q2);

        List<Quiz> list = quizRepository.findAllByOrderByIdAsc();
        assertEquals(2, list.size());

        assertTrue(list.get(0).getId() < list.get(1).getId());
    }

    @Test
    void testFetchFullQuiz() {
        // ✅ build Quiz with Questions + Options
        OptionChoice o1 = new OptionChoice();
        o1.setText("Option 1");
        o1.setCorrect(true);

        OptionChoice o2 = new OptionChoice();
        o2.setText("Option 2");
        o2.setCorrect(false);

        Question q = new Question();
        q.setText("What is Java?");
        q.setOptions(List.of(o1, o2));

        o1.setQuestion(q);
        o2.setQuestion(q);

        Quiz quiz = new Quiz();
        quiz.setTitle("Java Quiz");
        quiz.setQuestions(List.of(q));

        q.setQuiz(quiz);

        // ✅ save cascade (if cascade exists), or save manually in correct order
        quiz = quizRepository.save(quiz);

        Optional<Quiz> full = quizRepository.fetchFullQuiz(quiz.getId());

        assertTrue(full.isPresent());

        Quiz loaded = full.get();
        assertEquals("Java Quiz", loaded.getTitle());
        assertNotNull(loaded.getQuestions());
        assertEquals(1, loaded.getQuestions().size());

        Question loadedQ = loaded.getQuestions().get(0);
        assertEquals(2, loadedQ.getOptions().size());
    }

    @Test
    void testFetchFullQuiz_NotFound() {
        Optional<Quiz> result = quizRepository.fetchFullQuiz(999L);
        assertTrue(result.isEmpty());
    }
}

