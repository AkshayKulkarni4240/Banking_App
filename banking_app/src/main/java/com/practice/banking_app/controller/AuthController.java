package com.practice.banking_app.controller;

import com.practice.banking_app.dto.LoginRequest;
import com.practice.banking_app.dto.RegisterRequest;
import com.practice.banking_app.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody RegisterRequest request) {
        authService.register(request);
        return ResponseEntity.ok("User registered successfully");
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody LoginRequest request) {
        // Get token and user info from auth service
        Map<String, Object> loginResponse = authService.login(request);
        
        return ResponseEntity.ok(loginResponse);
    }
}