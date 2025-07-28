package com.example.bookingsystem.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Entity
@Table(name = "user_packages")
public class UserPackage {
    @Id
    private UUID id;

    @ManyToOne
    private User user;

    @ManyToOne
    private Package pkg;

    private int creditsLeft;
    private LocalDateTime purchasedAt;
    private LocalDateTime expiresAt;
}
