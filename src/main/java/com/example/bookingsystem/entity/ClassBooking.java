package com.example.bookingsystem.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "class_bookings")
public class ClassBooking {

    public ClassBooking(User user, ClassSchedule schedule) {
        this.user = user;
        this.schedule = schedule;
        this.status = BookingStatus.BOOKED;
        this.bookedAt = LocalDateTime.now();
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private User user;

    @ManyToOne
    private ClassSchedule schedule;

    @Enumerated(EnumType.STRING)
    private BookingStatus status; // BOOKED, CANCELLED, WAITLISTED, ATTENDED, NO_SHOW

    private LocalDateTime bookedAt;
    private LocalDateTime checkInTime;

    public enum BookingStatus {
        BOOKED, CANCELLED, WAITLISTED, ATTENDED, NO_SHOW
    }
}
