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
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", updatable = false, nullable = false)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "pkg_id", referencedColumnName = "id")
    private Package pkg;

    private Integer remainingCredits;
    private LocalDateTime purchasedAt;
    private LocalDateTime expiresAt;

    public boolean isExpired() {
        return expiresAt.isBefore(LocalDateTime.now());
    }

    public void decreaseCredit() {
        if (remainingCredits <= 0) {
            throw new IllegalStateException("No credits left to deduct.");
        }
        this.remainingCredits--;
    }

    public void increaseCredit() {
        this.remainingCredits++;
    }

    @Enumerated(EnumType.STRING)
    private PackageStatus status;

    public enum PackageStatus {
        ACTIVE, EXPIRED
    }
}
