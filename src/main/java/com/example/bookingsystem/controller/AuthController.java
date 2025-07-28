package com.example.bookingsystem.controller;

import com.example.bookingsystem.dto.LoginRequest;
import com.example.bookingsystem.dto.UserDTO;
import com.example.bookingsystem.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@SuppressWarnings("unused")
@RestController
@RequestMapping("/api/auth")
@Tag(name = "Authentication", description = "APIs for login and register")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @Operation(summary = "User Login", description = "Logs in a user and returns JWT token.")
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest data) {
        String token = authService.login(data.getEmail(), data.getPassword()); // Password not needed as Basic Auth handled by Spring
        return ResponseEntity.ok().body(java.util.Map.of("token", token));
    }

    @Operation(summary = "User Registration", description = "Registers a new user account.")
    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody UserDTO data) {
        authService.register(data);
        return ResponseEntity.ok("User registered successfully");
    }
}
