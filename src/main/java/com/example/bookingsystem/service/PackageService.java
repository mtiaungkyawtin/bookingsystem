package com.example.bookingsystem.service;

import com.example.bookingsystem.dto.PackageResponseDTO;
import com.example.bookingsystem.dto.RequestPackage;
import com.example.bookingsystem.dto.UserPackageResponseDTO;
import com.example.bookingsystem.entity.User;
import com.example.bookingsystem.entity.UserPackage;
import com.example.bookingsystem.repository.PackageRepository;
import com.example.bookingsystem.repository.UserPackageRepository;
import org.springframework.http.ResponseEntity;
import com.example.bookingsystem.entity.Package;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class PackageService {
    private final PackageRepository packageRepo;
    private final UserPackageRepository userPackageRepo;

    public PackageService(PackageRepository packageRepo, UserPackageRepository userPackageRepo) {
        this.userPackageRepo = userPackageRepo;
        this.packageRepo = packageRepo;
    }

    public List<PackageResponseDTO> getAvailablePackages(String country) {
        return packageRepo.findByCountryAndIsActiveTrue(country).stream()
                .map(pkg -> new PackageResponseDTO(
                        pkg.getId(),
                        pkg.getName(),
                        pkg.getCountry(),
                        pkg.getCredits(),
                        pkg.getPrice(),
                        pkg.getDurationDays(),
                        pkg.isActive()
                )).collect(Collectors.toList());
    }

    public List<UserPackageResponseDTO> getUserPackages(UUID userId) {
        return userPackageRepo.findByUserId(userId).stream()
                .map(up -> {
                    Package pack = up.getPkg();
                    return new UserPackageResponseDTO(
                            up.getId(),
                            pack.getName(),
                            pack.getCountry(),
                            pack.getCredits(),
                            up.getRemainingCredits(),
                            up.getPurchasedAt(),
                            up.getExpiresAt(),
                            up.isExpired()
                    );
                }).collect(Collectors.toList());
    }

    public ResponseEntity<?> getAllPackages() {
        return ResponseEntity.ok(packageRepo.findAll());
    }

    public ResponseEntity<?> getPackageById(UUID packageId) {
        return packageRepo.findById(packageId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    public ResponseEntity<?> createPackage(RequestPackage newPackage) {
        if (newPackage == null) {
            return ResponseEntity.badRequest().body("Package data is required");
        }
        Package packageEntity = new Package();
        packageEntity.setName(newPackage.getName());
        packageEntity.setCountry(newPackage.getCountry());
        packageEntity.setCredits(newPackage.getCredits());
        packageEntity.setPrice(newPackage.getPrice());
        packageEntity.setDurationDays(newPackage.getDurationDays());
        Package savedPackage = packageRepo.save(packageEntity);
        return ResponseEntity.ok(savedPackage);
    }

    public ResponseEntity<?> purchasePackage(UUID packageId, User user) {
        Package pack = packageRepo.findById(packageId)
                .orElseThrow(() -> new RuntimeException("Package not found"));

        if (!pack.isActive()) {
            return ResponseEntity.badRequest().body("Package is not active");
        }
        // Check if user already has this package
        if (userPackageRepo.existsByUserIdAndPkgId(user.getId(), packageId)) {
            return ResponseEntity.badRequest().body("You already own this package");
        }
        // Create and save the user package
        UserPackage userPackage = new UserPackage();
        userPackage.setUser(user);
        userPackage.setPkg(pack);
        userPackage.setRemainingCredits(pack.getCredits());
        userPackage.setPurchasedAt(LocalDateTime.now());
        userPackage.setExpiresAt(LocalDateTime.now().plusDays(pack.getDurationDays()));
        userPackage.setStatus(UserPackage.PackageStatus.ACTIVE);
        userPackageRepo.save(userPackage);
        return ResponseEntity.ok("Package purchased successfully");
    }
}
