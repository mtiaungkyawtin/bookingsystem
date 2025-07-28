package com.example.bookingsystem.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.util.UUID;

@Data
@Entity
@Table(name = "packages")
public class Package {
    @Id
    private UUID id;

    private String name;
    private String country;
    private int credits;
    private double price;
    private int durationDays;
}
