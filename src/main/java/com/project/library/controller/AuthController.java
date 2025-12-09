package com.project.library.controller;

import com.project.library.entity.User;
import com.project.library.entity.Role;
import com.project.library.repository.UserRepository;
import com.project.library.security.JwtUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;

    // REGISTER
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody Map<String, String> req) {

        String name = req.get("name");
        String email = req.get("email");
        String password = req.get("password");

        if (userRepository.findByEmail(email).isPresent()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Email already exists"));
        }

        User user = User.builder()
                .name(name)
                .email(email)
                .password(passwordEncoder.encode(password))
                .role(Role.USER)
                .build();

        userRepository.save(user);

        // JWT token üret
        String token = jwtUtils.generateToken(
                org.springframework.security.core.userdetails.User.builder()
                        .username(user.getEmail())
                        .password(user.getPassword())
                        .authorities("ROLE_USER")
                        .build()
        );

        return ResponseEntity.ok(Map.of(
                "message", "User registered",
                "token", token,
                "userId", user.getId(),
                "role", "USER"
        ));
    }

    // LOGIN
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> body) {

        String email = body.get("email");
        String password = body.get("password");

        return userRepository.findByEmail(email)
                .map(user -> {
                    if (!passwordEncoder.matches(password, user.getPassword())) {
                        return ResponseEntity.status(401)
                                .body(Map.of("error", "Invalid password"));
                    }

                    // ROLE_ prefix eklemezsen Spring kabul etmiyor
                    String role = "ROLE_" + user.getRole().name();

                    String token = jwtUtils.generateToken(
                            org.springframework.security.core.userdetails.User.builder()
                                    .username(user.getEmail())
                                    .password(user.getPassword())
                                    .authorities(role)
                                    .build()
                    );

                    return ResponseEntity.ok(Map.of(
                            "message", "Login successful",
                            "token", token,
                            "userId", user.getId(),
                            "role", user.getRole().name()
                    ));

                })
                .orElse(ResponseEntity.status(404)
                        .body(Map.of("error", "User not found")));
    }

}
