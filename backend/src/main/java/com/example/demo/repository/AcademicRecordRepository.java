package com.example.demo.repository;

import com.example.demo.model.AcademicRecord;
import com.example.demo.model.AcademicRecordId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AcademicRecordRepository extends JpaRepository<AcademicRecord, AcademicRecordId> {
    List<AcademicRecord> findByStudentNumber(String studentNumber);
}

