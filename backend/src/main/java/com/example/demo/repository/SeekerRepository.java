package com.example.demo.repository;

import com.example.demo.model.Seeker;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SeekerRepository extends JpaRepository<Seeker,Integer> {
    Optional<Seeker> findByPhone(String phone);
}
