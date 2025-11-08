package com.example.Quiz_project.repository;

import com.example.Quiz_project.entity.OptionChoice;
import com.example.Quiz_project.entity.Question;
import com.example.Quiz_project.entity.Quiz;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class QuestionRepositoryTest {

    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private QuizRepository quizRepository;

    @Test
    void testFindByQuizId() {
        Quiz quiz = new Quiz();
        quiz.setTitle("Test Quiz");
        quiz = quizRepository.save(quiz);

        Question q1 = new Question();
        q1.setText("Question 1");
        q1.setQuiz(quiz);

        Question q2 = new Question();
        q2.setText("Question 2");
        q2.setQuiz(quiz);

        questionRepository.save(q1);
        questionRepository.save(q2);

        List<Question> result = questionRepository.findByQuizId(quiz.getId());

        assertEquals(2, result.size());
        assertEquals("Question 1", result.get(0).getText());
    }

    @Test
    void testFindByQuizIdFetch() {
        // ✅ create quiz
        Quiz quiz = new Quiz();
        quiz.setTitle("Fetch Quiz");
        quiz = quizRepository.save(quiz);

        // ✅ create question & options
        OptionChoice opt1 = new OptionChoice();
        opt1.setText("A");
        opt1.setCorrect(true);

        OptionChoice opt2 = new OptionChoice();
        opt2.setText("B");
        opt2.setCorrect(false);

        Question q = new Question();
        q.setText("Sample Question");
        q.setQuiz(quiz);
        q.setOptions(List.of(opt1, opt2));

        opt1.setQuestion(q);
        opt2.setQuestion(q);

        questionRepository.save(q);

        List<Question> result = questionRepository.findByQuizIdFetch(quiz.getId());

        assertEquals(1, result.size());
        Question loadedQ = result.get(0);
        assertEquals(2, loadedQ.getOptions().size());
    }

    @Test
    void testNoQuestionsFound() {
        List<Question> result = questionRepository.findByQuizId(999L);
        assertTrue(result.isEmpty());

        List<Question> result2 = questionRepository.findByQuizIdFetch(999L);
        assertTrue(result2.isEmpty());
    }
}

