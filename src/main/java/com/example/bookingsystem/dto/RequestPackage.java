package com.example.bookingsystem.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RequestPackage {
    private String name;
    private String country;
    private int credits;
    private double price;
    private int durationDays;
}
