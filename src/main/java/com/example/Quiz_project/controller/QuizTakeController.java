package com.example.Quiz_project.controller;

import com.example.Quiz_project.entity.Quiz;
import com.example.Quiz_project.repository.QuizResultRepository;
import com.example.Quiz_project.service.EmailService;
import com.example.Quiz_project.service.QuizResultService;
import com.example.Quiz_project.service.QuizService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@Controller
@RequiredArgsConstructor
@RequestMapping("/quiz")
public class QuizTakeController {

    private final QuizService quizService;
    private final QuizResultRepository quizResultRepo;
    private final QuizResultService resultService;
    private final EmailService emailService;


@GetMapping
public String showAvailable(Model model) {
    model.addAttribute("quizzes", quizService.listAll());
    return "participant_quiz_list";
}


    @GetMapping("/take/{id}")
    public String takeQuiz(@PathVariable Long id, Model model) {
        Quiz quiz = quizService.loadQuiz(id);
        model.addAttribute("quiz", quiz);
        return "quiz_take";
    }

   @PostMapping("/take/{id}/submit")
public String submitQuiz(
        @PathVariable Long id,
        @RequestParam Map<String, String> formData,
        Model model) {

    Map<Long, Long> answers = new HashMap<>();
    formData.forEach((key, value) -> {
        if (key.startsWith("q_")) {
            answers.put(Long.valueOf(key.substring(2)), Long.valueOf(value));
        }
    });

    var result = resultService.saveAttempt(id, "GuestUser", answers);

    model.addAttribute("result", result);
    return "quiz_result";
}



    @GetMapping("/results")
    public String showAllResults(Model model) {
        model.addAttribute("results", resultService.getAllResults());
        return "quiz-results"; // table view
    }

}

