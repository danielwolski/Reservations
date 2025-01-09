package com.calendarapp.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.calendarapp.auth.JwtService;
import com.calendarapp.model.User;
import com.calendarapp.service.UserService;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;
    private final JwtService jwtService;

    public UserController(UserService userService, JwtService jwtService) {
        this.userService = userService;
        this.jwtService = jwtService;
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody User user) {
        try {
            userService.registerUser(user);
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody User user) {
        boolean isValid = userService.validateUserCredentials(user.getUsername(), user.getPassword());
        if (isValid) {
            String token = jwtService.generateToken(user.getUsername());
            Map<String, String> response = new HashMap<>();
            response.put("token", token);
         return ResponseEntity.ok(response); 
        } else {
            return ResponseEntity.status(401).body("Invalid credentials");
        }
    }
}
