package com.example.bookingsystem.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.util.UUID;

@Data
@Entity
@Table(name = "users")
public class User {
    @Id
    private UUID id;

    @Column(unique = true, nullable = false)
    private String email;

    private String password;
    private String fullName;
    private String country;
}
