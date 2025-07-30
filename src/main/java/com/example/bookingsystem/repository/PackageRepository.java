package com.example.bookingsystem.repository;

import com.example.bookingsystem.entity.Package;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface PackageRepository extends JpaRepository<Package, UUID> {
    List<Package> findByCountryAndIsActiveTrue(String country);
}
