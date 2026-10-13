package com.example.demo.service;

import com.example.demo.dto.AcademicRecordDTO;

import java.util.List;

public interface TempAcademicRecordService {
    List<AcademicRecordDTO> getAllTempAcademicRecords();
    void deleteTempAcademicRecord(String studentNumber, String subjectName, String academicYear, String semester);
    void saveTempAcademicRecords(List<AcademicRecordDTO> records);
}
