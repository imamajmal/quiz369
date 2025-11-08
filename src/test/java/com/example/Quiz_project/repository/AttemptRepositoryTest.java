package com.example.Quiz_project.repository;

import com.example.Quiz_project.entity.Attempt;
import com.example.Quiz_project.entity.Quiz;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class AttemptRepositoryTest {

    @Autowired
    private AttemptRepository attemptRepository;

    @Autowired
    private QuizRepository quizRepository;

    @Test
    void testSaveAndFindByUsername() {
        // ✅ Create a quiz
        Quiz quiz = new Quiz();
        quiz.setTitle("Java Test");
        quiz = quizRepository.save(quiz);

        // ✅ Create attempts
        Attempt a1 = Attempt.builder()
                .quiz(quiz)
                .username("imam")
                .startedAt(Instant.now())
                .build();

        Attempt a2 = Attempt.builder()
                .quiz(quiz)
                .username("imam")
                .startedAt(Instant.now())
                .build();

        attemptRepository.save(a1);
        attemptRepository.save(a2);

        // ✅ Query
        List<Attempt> attempts = attemptRepository.findByUsername("imam");

        assertEquals(2, attempts.size());
        assertEquals("imam", attempts.get(0).getUsername());
    }

    @Test
    void testFindByUsername_Empty() {
        List<Attempt> attempts = attemptRepository.findByUsername("unknown");
        assertTrue(attempts.isEmpty());
    }

    @Test
    void testSaveAndFindById() {
        Quiz quiz = new Quiz();
        quiz.setTitle("Python Test");
        quiz = quizRepository.save(quiz);

        Attempt attempt = Attempt.builder()
                .quiz(quiz)
                .username("john")
                .startedAt(Instant.now())
                .build();

        Attempt saved = attemptRepository.save(attempt);

        var found = attemptRepository.findById(saved.getId());

        assertTrue(found.isPresent());
        assertEquals("john", found.get().getUsername());
    }

    @Test
    void testDeleteAttempt() {
        Quiz quiz = new Quiz();
        quiz.setTitle("Delete Test");
        quiz = quizRepository.save(quiz);

        Attempt attempt = Attempt.builder()
                .quiz(quiz)
                .username("ali")
                .startedAt(Instant.now())
                .build();

        Attempt saved = attemptRepository.save(attempt);

        attemptRepository.deleteById(saved.getId());

        assertTrue(attemptRepository.findById(saved.getId()).isEmpty());
    }
}
