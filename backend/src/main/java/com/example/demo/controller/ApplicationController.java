package com.example.demo.controller;

import com.example.demo.dto.ApplicationDTO;
import com.example.demo.model.Application;
import com.example.demo.service.ApplicationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/applications")
public class ApplicationController {
    @Autowired
    private ApplicationService applicationService;
    @PostMapping("/createApplication")
    public ResponseEntity<ApplicationDTO> createApplication(@RequestBody ApplicationDTO applicationDTO) {
        ApplicationDTO createdApplicationDTO = applicationService.createApplication(applicationDTO);
        return ResponseEntity.ok(createdApplicationDTO);
    }
    @GetMapping("getAllApplications")
    public ResponseEntity<List<ApplicationDTO>> getAllApplications() {
        List<ApplicationDTO> applicationsDTO = applicationService.getAllApplications();
        return ResponseEntity.ok(applicationsDTO);
    }
    @GetMapping("/{phone}")
    public ResponseEntity<List<ApplicationDTO>> getApplicationByPhone(@PathVariable String phone) {
        List<ApplicationDTO> applicationDTOs = applicationService.findBySeekerPhone(phone);
        return ResponseEntity.ok(applicationDTOs);
    }
    @PutMapping("/{id}/status")
    public ResponseEntity<ApplicationDTO> updateApplicationStatus(
            @PathVariable Integer id,
            @RequestParam Application.Status status,
            @RequestParam(required = false) String reviewNotes) {
        ApplicationDTO updatedApplicationDTO = applicationService.updateApplicationStatus(id, status, reviewNotes);
        if (updatedApplicationDTO != null) {
            return ResponseEntity.ok(updatedApplicationDTO);
        }
        return ResponseEntity.notFound().build();
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteApplication(@PathVariable Integer id) {
        applicationService.deleteApplication(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/student/{phone}/status/{status}")
    public ResponseEntity<List<ApplicationDTO>> getApplicationsByStudentPhoneAndStatus(
            @PathVariable String phone,
            @PathVariable Application.Status status) {
        List<ApplicationDTO> applicationDTOs = applicationService.findBySeekerPhoneAndStatus(phone, status);
        return ResponseEntity.ok(applicationDTOs);
    }

    @GetMapping("/recruiter/{recruiterPhone}")
    public ResponseEntity<List<ApplicationDTO>> getApplicationsByRecruiterPhone(@PathVariable String recruiterPhone) {
        List<ApplicationDTO> applicationDTOs = applicationService.findByRecruiterPhone(recruiterPhone);
        return ResponseEntity.ok(applicationDTOs);
    }

    @GetMapping("/id/{id}")
    public ResponseEntity<ApplicationDTO> getApplicationById(@PathVariable Integer id) {
        System.out.println("查询申请ID: " + id);
        Optional<ApplicationDTO> applicationDTO = applicationService.findApplicationById(id);
        if (applicationDTO.isPresent()) {
            return ResponseEntity.ok(applicationDTO.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/exists")
    public ResponseEntity<Boolean> checkApplicationExists(
            @RequestParam String phone,
            @RequestParam Integer jobId) {
        boolean exists = applicationService.existsApplication(phone, jobId);
        return ResponseEntity.ok(exists);
    }

    @GetMapping("/student/{studentPhone}/job/{jobId}")
    public ResponseEntity<ApplicationDTO> getApplicationByStudentPhoneAndJobId(
            @PathVariable String studentPhone,
            @PathVariable Integer jobId) {
        Optional<ApplicationDTO> applicationDTO = applicationService.findByStudentPhoneAndJobId(studentPhone, jobId);
        if (applicationDTO.isPresent()) {
            return ResponseEntity.ok(applicationDTO.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<ApplicationDTO> updateApplication(
            @PathVariable Integer id,
            @RequestBody ApplicationDTO applicationDTO) {
        try {
            ApplicationDTO updatedApplicationDTO = applicationService.updateApplication(id, applicationDTO);
            return ResponseEntity.ok(updatedApplicationDTO);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }


}
