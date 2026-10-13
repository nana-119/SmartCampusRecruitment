package com.example.demo.service;

import com.example.demo.dto.RecruiterDTO;
import com.example.demo.model.Recruiter;
import com.example.demo.repository.RecruiterRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Base64;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class RecruiterServiceImpl implements RecruiterService{

    private RecruiterRepository recruiterRepository;
    @Autowired
    public RecruiterServiceImpl(RecruiterRepository recruiterRepository) {
        this.recruiterRepository = recruiterRepository;
    }
    @Override
    @Transactional
    public List<RecruiterDTO> getAllRecruiters() {
        return recruiterRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public RecruiterDTO createRecruiter(RecruiterDTO recruiterDTO) {
        Recruiter recruiter = new Recruiter();
        recruiter.setPhone(recruiterDTO.getPhone());
        recruiter.setName(recruiterDTO.getName());
        recruiter.setPassword(recruiterDTO.getPassword());
        recruiter.setGender(recruiterDTO.getGender());
        recruiter.setCompany(recruiterDTO.getCompany());
        recruiter.setPosition(recruiterDTO.getPosition());
        if (recruiterDTO.getTouxiang() != null && !recruiterDTO.getTouxiang().isEmpty()) {
            try {
                recruiter.setTouxiang(Base64.getDecoder().decode(recruiterDTO.getTouxiang()));
            } catch (IllegalArgumentException e) {
                // 尝试使用URL安全Base64解码器
                recruiter.setTouxiang(Base64.getUrlDecoder().decode(recruiterDTO.getTouxiang()));
            }
        }
        Recruiter savedRecruiter = recruiterRepository.save(recruiter);
        return convertToDTO(savedRecruiter);
    }

    @Override
    @Transactional
    public RecruiterDTO update(String phone, RecruiterDTO updatedRecruiterDTO) {
        Recruiter recruiter = recruiterRepository.findByPhone(phone)
                .orElseThrow(() -> new RuntimeException("招聘者不存在"));
        recruiter.setName(updatedRecruiterDTO.getName());
        recruiter.setPhone(updatedRecruiterDTO.getPhone());
        recruiter.setPassword(updatedRecruiterDTO.getPassword());
        recruiter.setGender(updatedRecruiterDTO.getGender());
        recruiter.setCompany(updatedRecruiterDTO.getCompany());
        recruiter.setPosition(updatedRecruiterDTO.getPosition());
        if (updatedRecruiterDTO.getTouxiang() != null && !updatedRecruiterDTO.getTouxiang().isEmpty()) {
            try {
                recruiter.setTouxiang(Base64.getDecoder().decode(updatedRecruiterDTO.getTouxiang()));
            } catch (IllegalArgumentException e) {
                // 尝试使用URL安全Base64解码器
                recruiter.setTouxiang(Base64.getUrlDecoder().decode(updatedRecruiterDTO.getTouxiang()));
            }
        }
        Recruiter updatedRecruiter = recruiterRepository.save(recruiter);
        return convertToDTO(updatedRecruiter);

    }

    private RecruiterDTO convertToDTO(Recruiter recruiter) {
        RecruiterDTO recruiterDTO = new RecruiterDTO();
        recruiterDTO.setPhone(recruiter.getPhone());
        recruiterDTO.setName(recruiter.getName());
        recruiterDTO.setPassword(recruiter.getPassword());
        recruiterDTO.setGender(recruiter.getGender());
        recruiterDTO.setCompany(recruiter.getCompany());
        recruiterDTO.setPosition(recruiter.getPosition());
        recruiterDTO.setTouxiang(encodeByteArrayToBase64(recruiter.getTouxiang()));
        return recruiterDTO;
    }

    private String encodeByteArrayToBase64(byte[] touxiang) {
        if (touxiang == null) {
            return null; // 或者返回空字符串 ""
        }
        return Base64.getEncoder().encodeToString(touxiang);
    }

    @Override
    @Transactional
    public void delete(String phone) {
        Recruiter recruiter = recruiterRepository.findByPhone(phone)
                .orElseThrow(() -> new RuntimeException("招聘者不存在"));
        recruiterRepository.delete(recruiter);
    }

    @Override
    @Transactional
    public RecruiterDTO findByPhone(String phone) {
        return recruiterRepository.findByPhone(phone)
                .map(this::convertToDTO)
                .orElse(null);
    }

}
