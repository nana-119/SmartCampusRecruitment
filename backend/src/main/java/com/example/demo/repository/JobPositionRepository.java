package com.example.demo.repository;

import com.example.demo.model.JobPosition;
import com.example.demo.model.Recruiter;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface JobPositionRepository extends JpaRepository<JobPosition,Integer> {
    List<JobPosition> findByRecruiter_Phone(String recruiterPhone);
    List<JobPosition> findByRecruiter_PhoneAndStatus(String recruiterPhone, JobPosition.Status status);
}
