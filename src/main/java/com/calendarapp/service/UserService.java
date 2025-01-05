package com.calendarapp.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.calendarapp.model.User;
import com.calendarapp.repository.UserRepository;

import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public String registerUser(User user) {
        Optional<User> userExistingByUsername = userRepository.findByUsername(user.getUsername());
        if (userExistingByUsername.isPresent()) {
            throw new IllegalArgumentException("Username is already taken");
        }

        Optional<User> userExistingByEmail = userRepository.findByEmail(user.getEmail());
        if (userExistingByEmail.isPresent()) {
            throw new IllegalArgumentException("Email is already taken");
        }
        

        user.setPassword(passwordEncoder.encode(user.getPassword())); 
        userRepository.save(user);
        return "User registered successfully";
    }

    public boolean validateUserCredentials(String username, String password) {
        Optional<User> user = userRepository.findByUsername(username);
        if (user.isPresent()) {
            return passwordEncoder.matches(password, user.get().getPassword());
        }
        return false;
    }
}
