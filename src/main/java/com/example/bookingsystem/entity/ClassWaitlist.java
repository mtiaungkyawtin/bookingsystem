package com.example.bookingsystem.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
@Data
@Entity
@Table(name = "class_waitlists")
public class ClassWaitlist {

    public ClassWaitlist(User user, ClassSchedule schedule) {
        this.user = user;
        this.schedule = schedule;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private User user;

    @ManyToOne
    private ClassSchedule schedule;

    private LocalDateTime createdAt = LocalDateTime.now();
}
