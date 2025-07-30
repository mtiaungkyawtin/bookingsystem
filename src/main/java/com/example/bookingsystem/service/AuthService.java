package com.example.bookingsystem.service;

import com.example.bookingsystem.dto.LoginResponse;
import com.example.bookingsystem.dto.PasswordChangeRequest;
import com.example.bookingsystem.dto.ResetPasswordRequest;
import com.example.bookingsystem.dto.UserDTO;
import com.example.bookingsystem.entity.EmailVerificationToken;
import com.example.bookingsystem.entity.PasswordResetToken;
import com.example.bookingsystem.entity.User;
import com.example.bookingsystem.repository.EmailVerificationTokenRepository;
import com.example.bookingsystem.repository.PasswordResetTokenRepository;
import com.example.bookingsystem.repository.UserRepository;
import com.example.bookingsystem.security.JwtUtil;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class AuthService {
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final UserDetailsService userDetailsService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailVerificationTokenRepository emailTokenRepo;
    private final PasswordResetTokenRepository resetTokenRepo;
    private final JavaMailSender mailSender;

    public AuthService(AuthenticationManager authenticationManager, JwtUtil jwtUtil,
                       UserDetailsService userDetailsService, UserRepository userRepository,
                       PasswordEncoder passwordEncoder, JavaMailSender mailSender,
                       EmailVerificationTokenRepository emailTokenRepo,
                       PasswordResetTokenRepository resetTokenRepo) {
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
        this.userDetailsService = userDetailsService;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.emailTokenRepo = emailTokenRepo;
        this.mailSender = mailSender;
        this.resetTokenRepo = resetTokenRepo;
    }

    public LoginResponse login(String email, String password) {
        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(email, password)
        );
        if (auth.isAuthenticated()) {
            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("Invalid credentials"));
            UserDetails userDetails = userDetailsService.loadUserByUsername(email);
//            if (!user.isEmailVerified()) {
//                throw new RuntimeException("Email not verified.");
//            }
            String token = jwtUtil.generateToken(userDetails.getUsername());
            return new LoginResponse(token, user.getEmail(), user.getFullName());
        } else {
            throw new RuntimeException("Invalid credentials");
        }
    }

    public void register(UserDTO data) {
        if (userRepository.findByEmail(data.getEmail()).isPresent()) {
            throw new RuntimeException("Email already registered");
        }
        User user = new User();
        user.setId(UUID.randomUUID());
        user.setEmail(data.getEmail());
        user.setPassword(passwordEncoder.encode(data.getPassword()));
        user.setFullName(data.getFullName());
        user.setCountry(data.getCountry());
        user.setEmailVerified(false);
        userRepository.save(user);

        String token = UUID.randomUUID().toString();
        EmailVerificationToken emailToken = new EmailVerificationToken();
        emailToken.setVerificationToken(token);
        emailToken.setUser(user);
        emailToken.setVerificationTokenExpires(LocalDateTime.now().plusDays(1));
        emailTokenRepo.save(emailToken);

        String link = "http://localhost:8084/api/auth/verify-email?token=" + token;
        SimpleMailMessage msg = new SimpleMailMessage();
        msg.setTo(user.getEmail());
        msg.setSubject("Verify Your Email");
        msg.setText("Click this link to verify your email: " + link);
        mailSender.send(msg);
    }

    public String verifyEmail(String token) {
        EmailVerificationToken emailToken = emailTokenRepo.findByVerificationToken(token)
                .orElseThrow(() -> new RuntimeException("Invalid token"));

        if (emailToken.getVerificationTokenExpires().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Token expired");
        }

        User user = emailToken.getUser();
        user.setEmailVerified(true);
        userRepository.save(user);
        emailTokenRepo.delete(emailToken);
        return "Email verified successfully";
    }

    public void resetPassword(ResetPasswordRequest request) {
        PasswordResetToken token = resetTokenRepo.findByResetToken(request.getToken())
                .orElseThrow(() -> new RuntimeException("Invalid token"));

        if (token.getResetTokenExpires().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Token expired");
        }

        User user = token.getUser();
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
        resetTokenRepo.delete(token);
    }

    public void changePassword(String email, PasswordChangeRequest request) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!passwordEncoder.matches(request.getOldPassword(), user.getPassword())) {
            throw new RuntimeException("Incorrect old password.");
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
    }

    public UserDTO getProfile(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        UserDTO dto = new UserDTO();
        dto.setId(user.getId());
        dto.setEmail(user.getEmail());
        dto.setFullName(user.getFullName());
        dto.setCountry(user.getCountry());
        return dto;
    }
}
