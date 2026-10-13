package com.example.demo.service;

import com.example.demo.dto.AcademicRecordDTO;
import com.example.demo.model.AcademicRecordId;
import com.example.demo.model.TempAcademicRecord;
import com.example.demo.repository.TempAcademicRecordRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TempAcademicRecordServiceImpl implements TempAcademicRecordService{
    @Autowired
    private TempAcademicRecordRepository tempAcademicRecordRepository;

    @Override
    public List<AcademicRecordDTO> getAllTempAcademicRecords() {
        return tempAcademicRecordRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteTempAcademicRecord(String studentNumber, String subjectName, String academicYear, String semester) {
        AcademicRecordId id = new AcademicRecordId(studentNumber, subjectName, academicYear, semester);
        tempAcademicRecordRepository.deleteById(id);
    }

    @Override
    public void saveTempAcademicRecords(List<AcademicRecordDTO> records) {
        List<TempAcademicRecord> tempRecords = records.stream()
                .map(this::convertToEntity)
                .collect(Collectors.toList());

        tempAcademicRecordRepository.saveAll(tempRecords);
    }

    private AcademicRecordDTO convertToDTO(TempAcademicRecord tempRecord) {
        AcademicRecordDTO dto = new AcademicRecordDTO();
        dto.setStudentNumber(tempRecord.getStudentNumber());
        dto.setSubjectName(tempRecord.getSubjectName());
        dto.setAcademicYear(tempRecord.getAcademicYear());
        dto.setSemester(tempRecord.getSemester());
        dto.setScore(tempRecord.getScore());
        dto.setCredit(tempRecord.getCredit());
        return dto;
    }

    private TempAcademicRecord convertToEntity(AcademicRecordDTO dto) {
        AcademicRecordId id = new AcademicRecordId(
                dto.getStudentNumber(),
                dto.getSubjectName(),
                dto.getAcademicYear(),
                dto.getSemester()
        );

        TempAcademicRecord tempRecord = new TempAcademicRecord();
        tempRecord.setStudentNumber(id.getStudentNumber());
        tempRecord.setSubjectName(id.getSubjectName());
        tempRecord.setAcademicYear(id.getAcademicYear());
        tempRecord.setSemester(id.getSemester());
        tempRecord.setScore(dto.getScore() != null ? dto.getScore() : BigDecimal.ZERO);
        tempRecord.setCredit(dto.getCredit() != null ? dto.getCredit() : BigDecimal.ZERO);

        return tempRecord;
    }
}
