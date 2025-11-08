package com.example.Quiz_project.controller;

import com.example.Quiz_project.service.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model; // ✅ Correct import
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Controller
@RequiredArgsConstructor
@RequestMapping("/emailview1")
public class EmailParticipantViewController {

    private final EmailService emailService;

    @PostMapping("/send")
    public String sendEmail(@RequestParam String to,
                            @RequestParam String quiz,
                            @RequestParam String score,
                            Model model) {

        emailService.sendEmail(
                to,
                "Your Quiz Result",
                "email_result",
                Map.of("quiz", quiz, "score", score)
        );

        model.addAttribute("to", to);
        model.addAttribute("quiz", quiz);
        model.addAttribute("score", score);

        return "email_participant_success"; // ✅ show success UI page
    }

    @GetMapping("/test")
    public String testPage(Model model) {
        return "email_parti_test"; // ✅ load modern form template
    }
}
