package com.project.library.service.impl;

import com.project.library.entity.User;
import com.project.library.repository.UserRepository;
import com.project.library.service.UserService;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public User register(User user) {
        return userRepository.save(user);
    }

    @Override
    public User login(String email, String password) {

        User user = userRepository.findByEmail(email).orElse(null);

        if (user == null) return null;
        if (!user.getPassword().equals(password)) return null;

        return user;
    }

    @Override
    public User getById(Long id) {
        return userRepository.findById(id).orElse(null);
    }
}
