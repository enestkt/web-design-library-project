package com.project.library.dto.auth;

import lombok.Data;

@Data
public class AuthLoginRequest {
    private String email;
    private String password;
}
