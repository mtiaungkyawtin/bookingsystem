package com.example.bookingsystem.repository;

import com.example.bookingsystem.entity.UserPackage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface UserPackageRepository extends JpaRepository<UserPackage, UUID> {
    List<UserPackage> findByUserId(UUID userId);

    boolean existsByUserIdAndPkgId(UUID userId, UUID pkgId);

    List<UserPackage> findByUser_IdAndUser_Country(UUID userId, String country);
}
