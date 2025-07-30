package com.example.bookingsystem.controller;

import com.example.bookingsystem.dto.*;
import com.example.bookingsystem.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

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
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest data) {
        LoginResponse loginResponse = authService.login(data.getEmail(), data.getPassword()); // Password not needed as Basic Auth handled by Spring
        return ResponseEntity.ok().body(loginResponse);
    }

    @Operation(summary = "User Registration", description = "Registers a new user account.")
    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody UserDTO data) {
        authService.register(data);
        return ResponseEntity.ok("User registered successfully, please check your email to verify your account.");
    }

    @GetMapping("/verify-email")
    public ResponseEntity<?> verifyEmail(@RequestParam String token) {
        String result = authService.verifyEmail(token);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody ResetPasswordRequest request) {
        authService.resetPassword(request);
        return ResponseEntity.ok("Password reset successful.");
    }

    @PutMapping("/change-password")
    public ResponseEntity<?> changePassword(@RequestBody PasswordChangeRequest request,
                                            Authentication authentication) {
        authService.changePassword(authentication.getName(), request);
        return ResponseEntity.ok("Password changed successfully.");
    }

    @GetMapping("/profile")
    public ResponseEntity<UserDTO> getProfile(Authentication authentication) {
        return ResponseEntity.ok(authService.getProfile(authentication.getName()));
    }
}
