package com.example.bookingsystem.repository;

import com.example.bookingsystem.entity.ClassWaitlist;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ClassWaitlistRepository extends JpaRepository<ClassWaitlist, Long> {
    Optional<ClassWaitlist> findByUser_IdAndSchedule_Id(UUID userId, Long scheduleId);

    List<ClassWaitlist> findBySchedule_IdOrderByCreatedAtAsc(Long scheduleId);
}
