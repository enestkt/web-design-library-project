package com.project.library.service;

import com.project.library.entity.User;

public interface UserService {
    User register(User user);
    User login(String email, String password);
    User getById(Long id);
}
