package com.example.demo.service;

import com.example.demo.dto.ApplicationDTO;
import com.example.demo.model.Application;

import java.util.List;
import java.util.Optional;

public interface ApplicationService {
    ApplicationDTO createApplication(ApplicationDTO applicationDTO);
    List<ApplicationDTO> getAllApplications();
    List<ApplicationDTO> findBySeekerPhone(String phone);
    ApplicationDTO updateApplicationStatus(Integer id, Application.Status status, String reviewNotes);
    void deleteApplication(Integer id);
    List<ApplicationDTO> findBySeekerPhoneAndStatus(String phone, Application.Status status);
    List<ApplicationDTO> findByRecruiterPhone(String recruiterPhone);
    Optional<ApplicationDTO> findApplicationById(Integer id);
    boolean existsApplication(String phone, Integer jobId);
    Optional<ApplicationDTO> findByStudentPhoneAndJobId(String studentPhone, Integer jobId);
    ApplicationDTO updateApplication(Integer id, ApplicationDTO applicationDTO);





}
