package com.example.bookingsystem.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ClassWaitlistDTO {
    private Long id;
    private Long userId;
    private Long scheduleId;
    private LocalDateTime createdAt;
}
