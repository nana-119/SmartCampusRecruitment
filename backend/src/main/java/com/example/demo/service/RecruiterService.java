package com.example.demo.service;

import com.example.demo.dto.RecruiterDTO;
import com.example.demo.model.Recruiter;

import java.util.List;

public interface RecruiterService {
    List<RecruiterDTO> getAllRecruiters();
    RecruiterDTO createRecruiter(RecruiterDTO recruiterDTO);
    RecruiterDTO update(String phone, RecruiterDTO updatedRecruiter);
    void delete(String phone);
    RecruiterDTO findByPhone(String phone);
}
