package com.project.library.controller;

import com.project.library.entity.Role;
import com.project.library.entity.User;
import com.project.library.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class AuthController {

    @Autowired
    private UserService userService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping("/register")
    public String showRegisterPage(Model model) {
        model.addAttribute("user", new User());
        return "register";
    }

    @PostMapping("/register")
    public String register(User user, Model model) {

        // Şifreyi encode et
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        // Varsayılan rol
        user.setRole(Role.USER);

        userService.register(user);

        model.addAttribute("success", true);

        return "register";
    }
}
