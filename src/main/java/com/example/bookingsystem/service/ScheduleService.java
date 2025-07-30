package com.example.bookingsystem.service;

import com.example.bookingsystem.dto.ClassScheduleDTO;
import com.example.bookingsystem.entity.*;
import com.example.bookingsystem.repository.ClassScheduleRepository;
import com.example.bookingsystem.repository.ClassWaitlistRepository;
import com.example.bookingsystem.repository.ClassBookingRepository;
import com.example.bookingsystem.repository.UserPackageRepository;
import jakarta.transaction.Transactional;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class ScheduleService {
    private final ClassScheduleRepository scheduleRepo;
    private final ClassBookingRepository bookingRepo;
    private final ClassWaitlistRepository waitlistRepo;
    private final UserPackageRepository userPackageRepo;

    public ScheduleService(ClassScheduleRepository scheduleRepo, ClassBookingRepository bookingRepo,
                           ClassWaitlistRepository waitlistRepo, UserPackageRepository userPackageRepo) {
        this.scheduleRepo = scheduleRepo;
        this.bookingRepo = bookingRepo;
        this.waitlistRepo = waitlistRepo;
        this.userPackageRepo = userPackageRepo;
    }

    //createScheduleClass
    @Transactional
    public ResponseEntity<?> createScheduleClass(ClassScheduleDTO dto) {
        ClassSchedule schedule = new ClassSchedule();
        schedule.setTitle(dto.getTitle());
        schedule.setDescription(dto.getDescription());
        schedule.setCountry(dto.getCountry());
        schedule.setCapacity(dto.getCapacity());
        schedule.setStartTime(dto.getStartTime());
        schedule.setEndTime(dto.getEndTime());
        scheduleRepo.save(schedule);
        return ResponseEntity.ok("Class schedule created successfully");
    }

    public List<ClassScheduleDTO> getAvailableSchedules(String countryCode) {
        return scheduleRepo.findByCountryAndStartTimeAfter(countryCode, LocalDateTime.now())
                .stream()
                .map(this::scheduleToDTO)
                .toList();
    }

    @Transactional
    public ResponseEntity<?> bookClass(Long scheduleId, User user) {
        ClassSchedule schedule = scheduleRepo.findById(scheduleId).orElseThrow();

        // Check remaining credit
        UserPackage userPackage = userPackageRepo.findByUser_IdAndUser_Country(user.getId(), schedule.getCountry()).get(0);
        if (userPackage.getRemainingCredits() < 1) {
            throw new RuntimeException("Insufficient credits");
        }
        long currentBookings = bookingRepo.findBySchedule_Id(scheduleId).size();
        if (currentBookings >= schedule.getCapacity()) {
            //Booking Full, Add to waitlist
            ClassWaitlist waitlist = new ClassWaitlist(user, schedule);
            waitlistRepo.save(waitlist);
            return ResponseEntity.ok("Class full. Added to waitlist.");
        } else {
            // Book the class
            ClassBooking booking = new ClassBooking(user, schedule);
            bookingRepo.save(booking);
            userPackage.decreaseCredit();
            userPackageRepo.save(userPackage);
            return ResponseEntity.ok("Booked successfully");
        }
    }

    @Transactional
    public ResponseEntity<?> cancelBooking(Long scheduleId, User user) {
        ClassBooking booking = bookingRepo.findByUser_IdAndSchedule_Id(user.getId(), scheduleId).orElseThrow();
        bookingRepo.delete(booking);

        ClassSchedule schedule = booking.getSchedule();
        // check refund period within 4 hours
        if(isCancellationWithinRefundPeriod(schedule.getStartTime())) {
            // Refund credit
            UserPackage userPackage = userPackageRepo.findByUser_IdAndUser_Country(user.getId(), booking.getSchedule().getCountry()).stream().findFirst().orElseThrow();

            userPackage.increaseCredit();
            userPackageRepo.save(userPackage);
        }
        // Promote waitlist user
        ClassWaitlist next = waitlistRepo.findBySchedule_IdOrderByCreatedAtAsc(booking.getSchedule().getId()).get(0);
        if (next != null) {
            ClassBooking newBooking = new ClassBooking(next.getUser(), booking.getSchedule());
            bookingRepo.save(newBooking);
            userPackageRepo.findByUser_IdAndUser_Country(next.getUser().getId(), booking.getSchedule().getCountry()).stream().findFirst()
                    .ifPresent(pkg -> {
                        pkg.decreaseCredit();
                        userPackageRepo.save(pkg);
                    });
            waitlistRepo.delete(next);
        }
        return ResponseEntity.ok("Cancelled and handled waitlist");
    }

    public ResponseEntity<?> checkIn(Long scheduleId, User user) {
        ClassBooking booking = bookingRepo.findByUser_IdAndSchedule_Id(user.getId(), scheduleId)
                .orElseThrow();
        if (!booking.getSchedule().getStartTime().isBefore(LocalDateTime.now())) {
            return ResponseEntity.badRequest().body("Class hasn't started");
        }
        booking.setStatus(ClassBooking.BookingStatus.ATTENDED);
        booking.setCheckInTime(LocalDateTime.now());
        bookingRepo.save(booking);
        return ResponseEntity.ok("Checked in successfully");
    }

    @Transactional
    public ResponseEntity<?> endClass(Long scheduleId) {
        List<ClassWaitlist> waitlisted = waitlistRepo.findBySchedule_IdOrderByCreatedAtAsc(scheduleId);
        for (ClassWaitlist wait : waitlisted) {
            userPackageRepo.findByUser_IdAndUser_Country(wait.getUser().getId(), wait.getSchedule().getCountry()).stream().findFirst()
                    .ifPresent(pkg -> {
                        pkg.increaseCredit();
                        userPackageRepo.save(pkg);
                    });
        }
        waitlistRepo.deleteAll(waitlisted);
        return ResponseEntity.ok("Class ended. Waitlist refunded.");
    }

    private boolean isCancellationWithinRefundPeriod(LocalDateTime classStartTime) {
        return Duration.between(LocalDateTime.now(), classStartTime).toHours() >= 4;
    }


    private ClassScheduleDTO scheduleToDTO(ClassSchedule schedule) {
        return new ClassScheduleDTO(
                schedule.getId(),
                schedule.getTitle(),
                schedule.getDescription(),
                schedule.getCountry(),
                schedule.getStartTime(),
                schedule.getEndTime(),
                schedule.getCapacity()
        );
    }
}
