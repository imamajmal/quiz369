package com.example.Quiz_project.repository;

import com.example.Quiz_project.entity.QuizResult;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class QuizResultRepositoryTest {

    @Autowired
    private QuizResultRepository repo;

    @Test
    void testSaveAndFindAllSorted() {
        QuizResult r1 = QuizResult.builder()
                .userName("imam")
                .score(3)
                .totalQuestions(5)
                .submittedAt(LocalDateTime.now().minusMinutes(5))
                .build();

        QuizResult r2 = QuizResult.builder()
                .userName("imam")
                .score(4)
                .totalQuestions(5)
                .submittedAt(LocalDateTime.now())
                .build();

        repo.save(r1);
        repo.save(r2);

        List<QuizResult> list = repo.findAllByOrderBySubmittedAtDesc();

        assertEquals(2, list.size());
        assertTrue(list.get(0).getSubmittedAt().isAfter(list.get(1).getSubmittedAt()));
    }

    @Test
    void testFindByUserSorted() {
        QuizResult r1 = QuizResult.builder()
                .userName("john")
                .score(2)
                .submittedAt(LocalDateTime.now().minusMinutes(10))
                .build();

        QuizResult r2 = QuizResult.builder()
                .userName("john")
                .score(5)
                .submittedAt(LocalDateTime.now())
                .build();

        QuizResult r3 = QuizResult.builder()  // different user
                .userName("adam")
                .score(3)
                .submittedAt(LocalDateTime.now())
                .build();

        repo.save(r1);
        repo.save(r2);
        repo.save(r3);

        List<QuizResult> list = repo.findByUserNameOrderBySubmittedAtDesc("john");

        assertEquals(2, list.size());
        assertEquals("john", list.get(0).getUserName());
        assertTrue(list.get(0).getSubmittedAt().isAfter(list.get(1).getSubmittedAt()));
    }

    @Test
    void testFindByUserEmpty() {
        List<QuizResult> list = repo.findByUserNameOrderBySubmittedAtDesc("unknown");
        assertTrue(list.isEmpty());
    }
}

