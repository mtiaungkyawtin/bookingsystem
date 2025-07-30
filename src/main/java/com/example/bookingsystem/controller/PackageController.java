package com.example.bookingsystem.controller;

import com.example.bookingsystem.dto.PackageResponseDTO;
import com.example.bookingsystem.dto.RequestPackage;
import com.example.bookingsystem.dto.UserPackageResponseDTO;
import com.example.bookingsystem.entity.User;
import com.example.bookingsystem.security.UserPrincipal;
import com.example.bookingsystem.service.PackageService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@SuppressWarnings("unused")
@RestController
@RequestMapping("/api/packages")
@Tag(name = "Package Management", description = "APIs for managing packages")
public class PackageController {
    private final PackageService packageService;

    public PackageController(PackageService packageService) {
        this.packageService = packageService;
    }

    @GetMapping("/all")
    public ResponseEntity<?> getAllPackages() {
        return packageService.getAllPackages();
    }

    @GetMapping("/{packageId}")
    public ResponseEntity<?> getPackageById(@RequestParam UUID packageId) {
        return packageService.getPackageById(packageId);
    }

    @PostMapping("/create")
    public ResponseEntity<?> createPackage(@RequestBody RequestPackage newPackage) {
        return packageService.createPackage(newPackage);
    }

    @GetMapping("/available/{country}")
    public ResponseEntity<List<PackageResponseDTO>> getAvailablePackages(@RequestParam String country) {
        return ResponseEntity.ok(packageService.getAvailablePackages(country));
    }

    @PostMapping("/purchase")
    public ResponseEntity<?> purchasePackage(@RequestParam UUID packageId, @AuthenticationPrincipal UserPrincipal userPrincipal) {
        User user = userPrincipal.getUser();
        return packageService.purchasePackage(packageId, user);
    }

    @GetMapping("/my-owned-packages")
    public ResponseEntity<List<UserPackageResponseDTO>> getMyPackages(@AuthenticationPrincipal UserPrincipal userPrincipal) {
        User user = userPrincipal.getUser();
        return ResponseEntity.ok(packageService.getUserPackages(user.getId()));
    }
}
