package com.project.library.service;

import com.project.library.dto.auth.AuthLoginRequest;
import com.project.library.dto.auth.AuthRegisterRequest;
import com.project.library.dto.auth.AuthResponse;

public interface AuthService {

    AuthResponse register(AuthRegisterRequest request);

    AuthResponse login(AuthLoginRequest request);
}
