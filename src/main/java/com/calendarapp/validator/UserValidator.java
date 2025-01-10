package com.calendarapp.validator;

import java.util.Optional;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.calendarapp.exception.UserLoginException;
import com.calendarapp.exception.UserRegistrationException;
import com.calendarapp.model.User;
import com.calendarapp.repository.UserRepository;

@Component
public class UserValidator {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserValidator(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public User validateAndGetUser(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UserLoginException("User " + username + " not found"));
    }
    
    public void validateUserCredentials(String username, String password) {
        Optional<User> user = userRepository.findByUsername(username);
        if (!user.isPresent() || !passwordEncoder.matches(password, user.get().getPassword())) {
            throw new UserLoginException("Invalid credentials");
        }        
    }

    public void validateRegistration(User user) {
        if (userRepository.findByUsername(user.getUsername()).isPresent()) {
            throw new UserRegistrationException("Username is already taken: " + user.getUsername());
        }
        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            throw new UserRegistrationException("Email is already taken: " + user.getEmail());
        }
    }
    
}
