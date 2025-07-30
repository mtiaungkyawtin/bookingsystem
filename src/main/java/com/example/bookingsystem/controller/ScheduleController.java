package com.example.bookingsystem.controller;

import com.example.bookingsystem.dto.ClassScheduleBookingRequest;
import com.example.bookingsystem.dto.ClassScheduleDTO;
import com.example.bookingsystem.dto.RequestPackage;
import com.example.bookingsystem.entity.User;
import com.example.bookingsystem.security.UserPrincipal;
import com.example.bookingsystem.service.ScheduleService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@SuppressWarnings("unused")
@RestController
@RequestMapping("/api/schedule")
@Tag(name = "Schedule Management", description = "Endpoints for managing class schedules")
public class ScheduleController {
    private final ScheduleService scheduleService;

    public ScheduleController(ScheduleService scheduleService) {
        this.scheduleService = scheduleService;
    }

    @PostMapping("/create-class")
    public ResponseEntity<?> createScheduleClass(@RequestBody ClassScheduleDTO data) {
        return scheduleService.createScheduleClass(data);
    }

    //View available class schedules by country
    @GetMapping("/available-classes")
    public List<ClassScheduleDTO> getAvailableSchedules(@RequestParam String country) {
        return scheduleService.getAvailableSchedules(country);
    }
    //Book a class
    @PostMapping("/book")
    public ResponseEntity<?> bookClass(@RequestBody ClassScheduleBookingRequest request, @AuthenticationPrincipal UserPrincipal principal) {
        User user = principal.getUser();

        return scheduleService.bookClass(request.getClassScheduleId(), user);
    }

    //Cancel a class booking
    @DeleteMapping("/cancel/{scheduleId}")
    public ResponseEntity<?> cancelBooking(@PathVariable Long scheduleId, @AuthenticationPrincipal UserPrincipal principal) {
        User user = principal.getUser();

        return scheduleService.cancelBooking(scheduleId, user);
    }

    @PutMapping("/check-in")
    public ResponseEntity<?> checkIn(@RequestBody ClassScheduleBookingRequest request, @AuthenticationPrincipal UserPrincipal principal) {
        User user = principal.getUser();

        return scheduleService.checkIn(request.getClassScheduleId(), user);
    }

    @PostMapping("/end-class")
    public ResponseEntity<?> endClass(@RequestBody ClassScheduleBookingRequest request) {

        return scheduleService.endClass(request.getClassScheduleId());
    }
}
