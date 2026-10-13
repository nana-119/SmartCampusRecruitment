package com.example.demo.controller;

import com.example.demo.dto.AcademicRecordDTO;
import com.example.demo.service.AcademicRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/academic-records")
public class AcademicRecordController {
    @Autowired
    private AcademicRecordService academicRecordService;

    @GetMapping
    public ResponseEntity<List<AcademicRecordDTO>> getAllAcademicRecords() {
        List<AcademicRecordDTO> records = academicRecordService.getAllAcademicRecords();
        return new ResponseEntity<>(records, HttpStatus.OK);
    }

    @GetMapping("/{studentNumber}/{subjectName}/{academicYear}/{semester}")
    public ResponseEntity<AcademicRecordDTO> getAcademicRecordById(
            @PathVariable String studentNumber,
            @PathVariable String subjectName,
            @PathVariable String academicYear,
            @PathVariable String semester) {

        AcademicRecordDTO record = academicRecordService.getAcademicRecordById(studentNumber, subjectName, academicYear, semester);

        if (record != null) {
            return new ResponseEntity<>(record, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping
    public ResponseEntity<AcademicRecordDTO> createAcademicRecord(@RequestBody AcademicRecordDTO academicRecordDTO) {
        AcademicRecordDTO createdRecord = academicRecordService.createAcademicRecord(academicRecordDTO);
        return new ResponseEntity<>(createdRecord, HttpStatus.CREATED);
    }

    @PutMapping("/{studentNumber}/{subjectName}/{academicYear}/{semester}")
    public ResponseEntity<AcademicRecordDTO> updateAcademicRecord(
            @PathVariable String studentNumber,
            @PathVariable String subjectName,
            @PathVariable String academicYear,
            @PathVariable String semester,
            @RequestBody AcademicRecordDTO academicRecordDTO) {

        AcademicRecordDTO updatedRecord = academicRecordService.updateAcademicRecord(studentNumber, subjectName, academicYear, semester, academicRecordDTO);

        if (updatedRecord != null) {
            return new ResponseEntity<>(updatedRecord, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/{studentNumber}/{subjectName}/{academicYear}/{semester}")
    public ResponseEntity<HttpStatus> deleteAcademicRecord(
            @PathVariable String studentNumber,
            @PathVariable String subjectName,
            @PathVariable String academicYear,
            @PathVariable String semester) {

        academicRecordService.deleteAcademicRecord(studentNumber, subjectName, academicYear, semester);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/student")
    public ResponseEntity<List<AcademicRecordDTO>> getAcademicRecordsBySchoolAndStudent(
            @RequestParam String studentNumber) {

        List<AcademicRecordDTO> records = academicRecordService.getAcademicRecordsByStudent( studentNumber);

        if (records != null) {
            return new ResponseEntity<>(records, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

}
