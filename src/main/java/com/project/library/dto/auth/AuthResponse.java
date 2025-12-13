package com.project.library.dto.auth;

import com.project.library.entity.Role;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AuthResponse {
    private String token;
    private Role role;
    private Long userId;
    private String message;
}
