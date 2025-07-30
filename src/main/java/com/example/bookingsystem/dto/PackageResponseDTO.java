package com.example.bookingsystem.dto;

import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PackageResponseDTO {
    private UUID id;
    private String name;
    private String country;
    private int credits;
    private double price;
    private int durationDays;
    private boolean isActive;
}
