package com.calendarapp.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.calendarapp.auth.JwtService;
import com.calendarapp.model.User;
import com.calendarapp.repository.UserRepository;
import com.calendarapp.validator.UserValidator;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final UserValidator userValidator;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public UserService(UserRepository userRepository, UserValidator userValidator, PasswordEncoder passwordEncoder, JwtService jwtService) {
        this.userRepository = userRepository;
        this.userValidator = userValidator;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    public void registerUser(User user) {
        userValidator.validateRegistration(user);
        user.setPassword(passwordEncoder.encode(user.getPassword())); 
        userRepository.save(user);
    }

    public String loginUser(User user) {
        userValidator.validateUserCredentials(user.getUsername(), user.getPassword());
        return jwtService.generateToken(user.getUsername());
    }
}
