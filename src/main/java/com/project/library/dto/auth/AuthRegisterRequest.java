package com.project.library.dto.auth;

import lombok.Data;

@Data
public class AuthRegisterRequest {
    private String name;
    private String email;
    private String password;
}
