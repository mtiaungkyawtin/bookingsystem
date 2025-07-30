package com.example.bookingsystem.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ClassScheduleDTO {
    private Long id;
    private String title;
    private String description;
    private String country;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Integer capacity;
}
