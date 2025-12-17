package com.project.library.controller;

import com.project.library.dto.auth.AuthLoginRequest;
import com.project.library.dto.auth.AuthRegisterRequest;
import com.project.library.entity.User;
import com.project.library.entity.Role;
import com.project.library.repository.UserRepository;
import com.project.library.security.JwtUtils;
import jakarta.validation.Valid; // ⭐ Validasyon importu
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

    // REGISTER - @Valid ve DTO Kullanımı
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody @Valid AuthRegisterRequest req) {

        // Validasyon başarılıysa buraya girer
        if (userRepository.findByEmail(req.getEmail()).isPresent()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Email already exists"));
        }

        User user = User.builder()
                .name(req.getName())      // DTO'dan alıyoruz
                .email(req.getEmail())    // DTO'dan alıyoruz
                .password(passwordEncoder.encode(req.getPassword())) // DTO'dan alıyoruz
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
                "message", "User registered successfully",
                "token", token,
                "userId", user.getId(),
                "role", "USER"
        ));
    }

    // LOGIN - Tutarlılık için burayı da DTO yapabiliriz ama Map de kalabilir.
    // Düzgün olması için AuthLoginRequest kullanıyorum.
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthLoginRequest req) {

        String email = req.getEmail();
        String password = req.getPassword();

        return userRepository.findByEmail(email)
                .map(user -> {
                    if (!passwordEncoder.matches(password, user.getPassword())) {
                        return ResponseEntity.status(401)
                                .body(Map.of("error", "Invalid password"));
                    }

                    // ROLE_ prefix eklemezsen Spring Security yetkilendirmesi çalışmaz
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