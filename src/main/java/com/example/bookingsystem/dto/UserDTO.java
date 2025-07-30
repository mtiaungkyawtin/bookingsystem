package com.example.bookingsystem.dto;

import lombok.Data;

import java.util.UUID;

@Data
public class UserDTO {
    private UUID id;
    private String email;
    private String password;
    private String fullName;
    private String country;
}
