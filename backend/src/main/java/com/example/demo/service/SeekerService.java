package com.example.demo.service;

import com.example.demo.dto.SeekerDTO;
import com.example.demo.model.Seeker;

import java.util.List;

public interface SeekerService {
    SeekerDTO createSeeker(SeekerDTO seekerDTO);
    List<SeekerDTO> getAllSeekers();
    SeekerDTO findByPhone(String phone);
    SeekerDTO update(String phone, SeekerDTO seekerDTO);
    void delete(String phone);
    SeekerDTO updateResumeByPhone(String phone, String resumeFile);
}
