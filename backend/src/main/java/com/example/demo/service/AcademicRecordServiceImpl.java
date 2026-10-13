package com.example.demo.service;

import com.example.demo.dto.AcademicRecordDTO;
import com.example.demo.model.AcademicRecord;
import com.example.demo.model.AcademicRecordId;
import com.example.demo.model.TempAcademicRecord;
import com.example.demo.repository.AcademicRecordRepository;
import com.example.demo.repository.TempAcademicRecordRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class AcademicRecordServiceImpl implements AcademicRecordService{
    @Autowired
    private AcademicRecordRepository academicRecordRepository;
    @Autowired
    private TempAcademicRecordRepository tempAcademicRecordRepository;

    @Override
    public List<AcademicRecordDTO> getAllAcademicRecords() {
        return academicRecordRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public AcademicRecordDTO getAcademicRecordById(String studentNumber, String subjectName, String academicYear, String semester) {
        AcademicRecordId id = new AcademicRecordId(studentNumber, subjectName, academicYear, semester);
        Optional<AcademicRecord> academicRecord = academicRecordRepository.findById(id);
        return academicRecord.map(this::convertToDTO).orElse(null);
    }

    @Override
    public AcademicRecordDTO createAcademicRecord(AcademicRecordDTO academicRecordDTO) {
        AcademicRecordId id = new AcademicRecordId(
                academicRecordDTO.getStudentNumber(),
                academicRecordDTO.getSubjectName(),
                academicRecordDTO.getAcademicYear(),
                academicRecordDTO.getSemester()
        );

        AcademicRecord academicRecord = new AcademicRecord();
        academicRecord.setStudentNumber(id.getStudentNumber());
        academicRecord.setSubjectName(id.getSubjectName());
        academicRecord.setAcademicYear(id.getAcademicYear());
        academicRecord.setSemester(id.getSemester());
        if (academicRecordDTO.getScore()==null){
            academicRecord.setScore(BigDecimal.ZERO);
        }else {
            academicRecord.setScore(academicRecordDTO.getScore());
        }
        if (academicRecordDTO.getCredit()==null){
            academicRecord.setCredit(BigDecimal.ZERO);
        }else {
            academicRecord.setCredit(academicRecordDTO.getCredit());
        }

        AcademicRecord savedRecord = academicRecordRepository.save(academicRecord);
        return convertToDTO(savedRecord);
    }

    @Override
    @Transactional
    public AcademicRecordDTO updateAcademicRecord(String studentNumber, String subjectName, String academicYear, String semester, AcademicRecordDTO academicRecordDTO) {
        AcademicRecordId id = new AcademicRecordId(studentNumber, subjectName, academicYear, semester);
        Optional<AcademicRecord> existingRecord = academicRecordRepository.findById(id);

        if (existingRecord.isPresent()) {
            // 先删除现有记录
            academicRecordRepository.deleteById(id);

            // 清空持久化上下文，确保没有缓存的实体状态
            academicRecordRepository.flush();

            // 创建新记录
            AcademicRecord academicRecord = new AcademicRecord();
            academicRecord.setStudentNumber(studentNumber);
            academicRecord.setSubjectName(subjectName);
            academicRecord.setAcademicYear(academicYear);
            academicRecord.setSemester(semester);
            academicRecord.setScore(academicRecordDTO.getScore() != null ? academicRecordDTO.getScore() : BigDecimal.ZERO);
            academicRecord.setCredit(academicRecordDTO.getCredit() != null ? academicRecordDTO.getCredit() : BigDecimal.ZERO);

            AcademicRecord updatedRecord = academicRecordRepository.save(academicRecord);
            return convertToDTO(updatedRecord);
        }
        return null;
    }

    @Override
    public void deleteAcademicRecord(String studentNumber, String subjectName, String academicYear, String semester) {
        AcademicRecordId id = new AcademicRecordId(studentNumber, subjectName, academicYear, semester);
        academicRecordRepository.deleteById(id);
    }

    @Override
    public List<AcademicRecordDTO> getAcademicRecordsByStudent(String studentNumber) {
        return academicRecordRepository.findByStudentNumber(studentNumber)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    private AcademicRecordDTO convertToDTO(AcademicRecord academicRecord) {
        AcademicRecordDTO dto = new AcademicRecordDTO();
        dto.setStudentNumber(academicRecord.getStudentNumber());
        dto.setSubjectName(academicRecord.getSubjectName());
        dto.setAcademicYear(academicRecord.getAcademicYear());
        dto.setSemester(academicRecord.getSemester());
        dto.setScore(academicRecord.getScore() != null ? academicRecord.getScore() : BigDecimal.ZERO);
        dto.setCredit(academicRecord.getCredit() != null ? academicRecord.getCredit() : BigDecimal.ZERO);
        return dto;
    }

    @Override
    public void importAcademicRecords(List<AcademicRecordDTO> records) {
        for (AcademicRecordDTO record : records) {
            TempAcademicRecord tempRecord = new TempAcademicRecord();
            tempRecord.setStudentNumber(record.getStudentNumber());
            tempRecord.setSubjectName(record.getSubjectName());
            tempRecord.setAcademicYear(record.getAcademicYear());
            tempRecord.setSemester(record.getSemester());
            tempRecord.setScore(record.getScore() != null ? record.getScore() : BigDecimal.ZERO);
            tempRecord.setCredit(record.getCredit() != null ? record.getCredit() : BigDecimal.ZERO);
            tempAcademicRecordRepository.save(tempRecord);
        }
    }
}
