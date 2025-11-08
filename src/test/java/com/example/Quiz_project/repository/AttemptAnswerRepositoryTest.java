package com.example.Quiz_project.repository;

import com.example.Quiz_project.entity.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.Instant;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class AttemptAnswerRepositoryTest {

    @Autowired
    private AttemptAnswerRepository attemptAnswerRepository;

    @Autowired
    private AttemptRepository attemptRepository;

    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private OptionRepository optionRepository;

    @Autowired
    private QuizRepository quizRepository;

    @Test
    void testSaveAndFind() {
        // ✅ Create quiz
        Quiz quiz = new Quiz();
        quiz.setTitle("Sample Quiz");
        quiz = quizRepository.save(quiz);

        // ✅ Create question
        Question q = new Question();
        q.setText("What is Java?");
        q.setQuiz(quiz);
        q = questionRepository.save(q);

        // ✅ Create option
        OptionChoice opt = new OptionChoice();
        opt.setText("Programming Language");
        opt.setCorrect(true);
        opt.setQuestion(q);
        opt = optionRepository.save(opt);

        // ✅ Create attempt
        Attempt attempt = Attempt.builder()
                .quiz(quiz)
                .username("imam")
                .startedAt(Instant.now())
                .build();
        attempt = attemptRepository.save(attempt);

        // ✅ Create AttemptAnswer
        AttemptAnswer aa = AttemptAnswer.builder()
                .attempt(attempt)
                .question(q)
                .selectedOption(opt)
                .correct(true)
                .build();

        AttemptAnswer saved = attemptAnswerRepository.save(aa);

        Optional<AttemptAnswer> found = attemptAnswerRepository.findById(saved.getId());

        assertTrue(found.isPresent());
        assertEquals("imam", found.get().getAttempt().getUsername());
        assertEquals("What is Java?", found.get().getQuestion().getText());
        assertEquals("Programming Language", found.get().getSelectedOption().getText());
        assertTrue(found.get().isCorrect());
    }

    @Test
    void testDelete() {
        Quiz quiz = quizRepository.save(new Quiz());

        Question question = new Question();
        question.setQuiz(quiz);
        question = questionRepository.save(question);

        OptionChoice option = new OptionChoice();
        option.setQuestion(question);
        option = optionRepository.save(option);

        Attempt attempt = attemptRepository.save(
                Attempt.builder().quiz(quiz).username("user").startedAt(Instant.now()).build()
        );

        AttemptAnswer aa = attemptAnswerRepository.save(
                AttemptAnswer.builder()
                        .attempt(attempt)
                        .question(question)
                        .selectedOption(option)
                        .correct(false)
                        .build()
        );

        attemptAnswerRepository.deleteById(aa.getId());

        assertFalse(attemptAnswerRepository.findById(aa.getId()).isPresent());
    }
}

