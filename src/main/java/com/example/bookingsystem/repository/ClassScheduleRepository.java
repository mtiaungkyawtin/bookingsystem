package com.example.bookingsystem.repository;

import com.example.bookingsystem.entity.ClassSchedule;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface ClassScheduleRepository extends JpaRepository<ClassSchedule, Long> {
    List<ClassSchedule> findByCountryAndStartTimeAfter(String country, LocalDateTime now);
}
