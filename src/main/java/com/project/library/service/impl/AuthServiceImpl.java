package com.project.library.service.impl;

import com.project.library.dto.auth.AuthLoginRequest;
import com.project.library.dto.auth.AuthRegisterRequest;
import com.project.library.dto.auth.AuthResponse;
import com.project.library.entity.Role;
import com.project.library.entity.User;
import com.project.library.repository.UserRepository;
import com.project.library.security.JwtUtils;
import com.project.library.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;
    private final AuthenticationManager authenticationManager;

    @Override
    public AuthResponse register(AuthRegisterRequest request) {

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already registered");
        }

        User user = new User();
        user.setEmail(request.getEmail());
        user.setName(request.getName());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(Role.USER);

        userRepository.save(user);

        String token = jwtUtils.generateToken(user.getEmail());

        return new AuthResponse(token, user.getRole(), user.getId(), "Registration successful");
    }

    @Override
    public AuthResponse login(AuthLoginRequest request) {

        // Spring Security kimlik doğrulama
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        String token = jwtUtils.generateToken(user.getEmail());

        return new AuthResponse(token, user.getRole(), user.getId(), "Login successful");
    }
}
