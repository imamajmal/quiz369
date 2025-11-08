package com.example.Quiz_project.repository;

import com.example.Quiz_project.entity.OptionChoice;
import com.example.Quiz_project.entity.Question;
import com.example.Quiz_project.entity.Quiz;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class OptionRepositoryTest {

    @Autowired
    private OptionRepository optionRepository;

    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private QuizRepository quizRepository;

    @Test
    void testSaveAndFind() {
        OptionChoice option = new OptionChoice();
        option.setText("Option A");
        option.setCorrect(true);

        OptionChoice saved = optionRepository.save(option);

        Optional<OptionChoice> found = optionRepository.findById(saved.getId());

        assertTrue(found.isPresent());
        assertEquals("Option A", found.get().getText());
        assertTrue(found.get().isCorrect());
    }

    @Test
    void testDelete() {
        OptionChoice option = new OptionChoice();
        option.setText("Delete Me");
        OptionChoice saved = optionRepository.save(option);

        optionRepository.deleteById(saved.getId());

        assertTrue(optionRepository.findById(saved.getId()).isEmpty());
    }

    @Test
    void testSaveWithQuestion() {
        // Create quiz
        Quiz quiz = new Quiz();
        quiz.setTitle("Sample Quiz");
        quiz = quizRepository.save(quiz);

        // Create question
        Question q = new Question();
        q.setText("What is Java?");
        q.setQuiz(quiz);
        q = questionRepository.save(q);

        // Create option linked to question
        OptionChoice opt = new OptionChoice();
        opt.setText("A programming language");
        opt.setCorrect(true);
        opt.setQuestion(q);

        OptionChoice saved = optionRepository.save(opt);

        Optional<OptionChoice> found = optionRepository.findById(saved.getId());

        assertTrue(found.isPresent());
        assertNotNull(found.get().getQuestion());
        assertEquals("What is Java?", found.get().getQuestion().getText());
    }
}

