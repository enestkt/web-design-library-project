package com.project.library.controller;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping("/")
    public String home(Authentication auth, Model model) {

        // Giriş yapan kullanıcının email'i
        String email = auth.getName();

        model.addAttribute("email", email);

        return "dashboard";  // dashboard.html
    }
}
