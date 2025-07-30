package com.example.bookingsystem.dto;

import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserPackageDTO {
    private Long id;
    private String packageName;
    private Integer remainingCredits;
    private LocalDateTime purchasedAt;
    private LocalDateTime expiresAt;
    private String status;
}
