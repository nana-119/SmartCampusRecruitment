package com.example.demo.service;

import com.example.demo.dto.AcademicRecordDTO;
import com.example.demo.model.AcademicRecord;

import java.util.List;


public interface AcademicRecordService {
    List<AcademicRecordDTO> getAllAcademicRecords();
    AcademicRecordDTO getAcademicRecordById(String studentNumber, String subjectName, String academicYear, String semester);
    AcademicRecordDTO createAcademicRecord(AcademicRecordDTO academicRecordDTO);
    AcademicRecordDTO updateAcademicRecord(String studentNumber, String subjectName, String academicYear, String semester, AcademicRecordDTO academicRecordDTO);
    void deleteAcademicRecord(String studentNumber, String subjectName, String academicYear, String semester);
    List<AcademicRecordDTO> getAcademicRecordsByStudent( String studentNumber);
    void importAcademicRecords(List<AcademicRecordDTO> records);
}
