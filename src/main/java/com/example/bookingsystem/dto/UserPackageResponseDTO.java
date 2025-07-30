package com.example.bookingsystem.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserPackageResponseDTO {
    private Long id;
    private String packageName;
    private String country;
    private Integer totalCredits;
    private Integer remainingCredits;
    private LocalDateTime purchasedAt;
    private LocalDateTime expiresAt;
    private boolean expired;
}
