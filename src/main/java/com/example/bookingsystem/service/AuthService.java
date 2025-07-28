package com.example.bookingsystem.service;

import com.example.bookingsystem.dto.UserDTO;
import com.example.bookingsystem.entity.User;
import com.example.bookingsystem.repository.UserRepository;
import com.example.bookingsystem.security.JwtUtil;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class AuthService {
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final UserDetailsService userDetailsService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthService(AuthenticationManager authenticationManager, JwtUtil jwtUtil,
                       UserDetailsService userDetailsService, UserRepository userRepository,
                       PasswordEncoder passwordEncoder) {
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
        this.userDetailsService = userDetailsService;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public String login(String email, String password) {
        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(email, password)
        );
        if (auth.isAuthenticated()) {
            UserDetails userDetails = userDetailsService.loadUserByUsername(email);
            return jwtUtil.generateToken(userDetails.getUsername());
        } else {
            throw new RuntimeException("Invalid credentials");
        }
    }

    public void register(UserDTO data) {
        if (userRepository.findByEmail(data.email).isPresent()) {
            throw new RuntimeException("Email already registered");
        }
        User user = new User();
        user.setId(UUID.randomUUID());
        user.setEmail(data.email);
        user.setPassword(passwordEncoder.encode(data.password));
        user.setFullName(data.fullName);
        user.setCountry(data.country);
        userRepository.save(user);
    }
}
