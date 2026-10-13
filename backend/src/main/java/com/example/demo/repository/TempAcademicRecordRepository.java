package com.example.demo.repository;

import com.example.demo.model.AcademicRecordId;
import com.example.demo.model.TempAcademicRecord;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TempAcademicRecordRepository extends JpaRepository<TempAcademicRecord, AcademicRecordId> {
    List<TempAcademicRecord> findByStudentNumber(String studentNumber);
}
