package com.example.bookingsystem.repository;

import com.example.bookingsystem.entity.ClassBooking;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ClassBookingRepository extends JpaRepository<ClassBooking, Long> {
    Optional<ClassBooking> findByUser_IdAndSchedule_Id(UUID userId, Long scheduleId);

    List<ClassBooking> findBySchedule_Id(Long scheduleId);
}
