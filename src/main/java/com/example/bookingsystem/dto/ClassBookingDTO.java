package com.example.bookingsystem.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ClassBookingDTO {
    private Long id;
    private String scheduleTitle;
    private String status;
    private LocalDateTime bookedAt;
    private LocalDateTime checkInTime;
}
