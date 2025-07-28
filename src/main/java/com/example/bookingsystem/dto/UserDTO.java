package com.example.bookingsystem.dto;

import lombok.Data;

@Data
public class UserDTO {
    public String email;
    public String password;
    public String fullName;
    public String country;
}
