package com.example.Quiz_project.controller;

import com.example.Quiz_project.entity.Role;
import com.example.Quiz_project.entity.User;
import com.example.Quiz_project.service.UserService;
import lombok.RequiredArgsConstructor;

import java.util.List;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/")
@RequiredArgsConstructor
public class AuthWebController {

    private final UserService userService;

    @GetMapping("/login")
    public String showLoginPage() {
        return "login"; // login.html
    }

    @PostMapping("/login")
public String doLogin(@RequestParam String username,
                      @RequestParam String password,
                      Model model) {

    try {
        String token = userService.login(username, password);
        User user = userService.getByUsername(username);

        // ✅ Put user in Spring Security session
        UsernamePasswordAuthenticationToken authToken =
                new UsernamePasswordAuthenticationToken(
                        user.getUsername(),
                        null,
                        List.of(new SimpleGrantedAuthority("ROLE_" + user.getRole()))
                );

        SecurityContextHolder.getContext().setAuthentication(authToken);

        if (user.getRole() == Role.ADMIN) {
            return "redirect:/admin/dashboard";
        } else if (user.getRole() == Role.PARTICIPANT) {
            return "redirect:/participant/dashboard";
        }

        model.addAttribute("error", "Unknown role!");
        return "login";

    } catch (Exception e) {
        model.addAttribute("error", "Invalid username or password");
        return "login";
    }
}


    @GetMapping()
    public String showRegisterPage(Model model) {
        model.addAttribute("user", new User());
        return "register"; // register.html
    }

    @PostMapping("/register")
    public String doRegister(@ModelAttribute User user, Model model) {
        userService.register(user);
        model.addAttribute("success", "Registration successful. Please login.");
        return "login";
    }

    // ✅ ADD REDIRECT METHOD HERE
    @GetMapping("/redirect")
    public String redirectAfterLogin(Authentication auth) {

        if (auth == null) {
            return "redirect:/login";
        }

        String username = auth.getName();
        User user = userService.getByUsername(username);

        if (user.getRole() == Role.ADMIN) {
            return "redirect:/admin/dashboard";
        }

        if (user.getRole() == Role.PARTICIPANT) {
            return "redirect:/participant/dashboard";
        }

        return "redirect:/login";
    }
}

