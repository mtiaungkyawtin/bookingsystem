package com.example.bookingsystem.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "password_reset_tokens")
public class PasswordResetToken {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "reset_token", unique = true, nullable = false)
    private String resetToken;

    @OneToOne
    private User user;

    @Column(name = "reset_token_expires")
    private LocalDateTime resetTokenExpires;
}
