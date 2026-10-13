package com.example.demo.service;

import com.example.demo.dto.ApplicationDTO;
import com.example.demo.model.Application;
import com.example.demo.model.JobPosition;
import com.example.demo.model.Recruiter;
import com.example.demo.model.Seeker;
import com.example.demo.repository.ApplicationRepository;
import com.example.demo.repository.JobPositionRepository;
import com.example.demo.repository.SeekerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.time.LocalDateTime;
import java.util.Base64;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ApplicationServiceImpl implements ApplicationService{
    private ApplicationRepository applicationRepository;
    private SeekerRepository seekerRepository;
    private JobPositionRepository jobPositionRepository;
    @Autowired
    public ApplicationServiceImpl(ApplicationRepository applicationRepository,SeekerRepository seekerRepository, JobPositionRepository jobPositionRepository) {
        this.applicationRepository = applicationRepository;
        this.seekerRepository = seekerRepository;
        this.jobPositionRepository = jobPositionRepository;
    }

    @Override
    @Transactional
    public ApplicationDTO createApplication(ApplicationDTO applicationDTO) {
        Seeker seeker = seekerRepository.findByPhone(applicationDTO.getStudentPhone())
                .orElseThrow(() -> new RuntimeException("用户不存在"));
        JobPosition jobPosition = jobPositionRepository.findById(applicationDTO.getJobId())
                .orElseThrow(() -> new RuntimeException("职位不存在"));

        Application application = new Application();
        application.setJobPosition(jobPosition);
        application.setSeeker(seeker);
        application.setJobIntention(applicationDTO.getJobIntention());
        application.setExpectedSalary(applicationDTO.getExpectedSalary());
        if (applicationDTO.getResumeFile() != null) {
            try {
                application.setResumeFile(Base64.getDecoder().decode(applicationDTO.getResumeFile()));
            } catch (IllegalArgumentException e) {
                application.setResumeFile(Base64.getUrlDecoder().decode(applicationDTO.getResumeFile()));
            }
        }
        application.setApplicationTime(LocalDateTime.now());
        application.setStatus(applicationDTO.getStatus());

        Application savedApplication = applicationRepository.save(application);

        return convertToDTO(savedApplication);
    }
    @Transactional(readOnly = true)
    public ApplicationDTO convertToDTO(Application application) {
        ApplicationDTO applicationDTO = new ApplicationDTO();
        applicationDTO.setId(application.getId());

        // 使用安全的方式访问 JobPosition 属性
        if (application.getJobPosition() != null) {
            applicationDTO.setJobId(application.getJobPosition().getId());
            applicationDTO.setJobTitle(application.getJobPosition().getTitle());

            if (application.getJobPosition().getRecruiter() != null) {
                applicationDTO.setCompanyName(application.getJobPosition().getRecruiter().getCompany());
            } else {
                applicationDTO.setCompanyName("未知公司");
            }
        } else {
            applicationDTO.setJobId(null);
            applicationDTO.setJobTitle("职位信息不存在");
            applicationDTO.setCompanyName("公司信息不存在");
        }

        if (application.getSeeker() != null) {
            applicationDTO.setStudentPhone(application.getSeeker().getPhone());
            applicationDTO.setStudentName(application.getSeeker().getName());
        } else {
            applicationDTO.setStudentPhone("未知");
            applicationDTO.setStudentName("未知");
        }

        applicationDTO.setJobIntention(application.getJobIntention());
        applicationDTO.setExpectedSalary(application.getExpectedSalary());

        if (application.getResumeFile() != null) {
            applicationDTO.setResumeFile(Base64.getEncoder().encodeToString(application.getResumeFile()));
        } else {
            applicationDTO.setResumeFile("");
        }

        applicationDTO.setStatus(application.getStatus());
        applicationDTO.setApplicationTime(application.getApplicationTime());
        applicationDTO.setReviewTime(application.getReviewTime());
        applicationDTO.setReviewNotes(application.getReviewNotes());

        return applicationDTO;
    }

    @Override
    @Transactional(readOnly = true)
    public List<ApplicationDTO> getAllApplications() {
        return applicationRepository.findAllWithDetails().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<ApplicationDTO> findBySeekerPhone(String phone) {
        return applicationRepository.findBySeekerPhoneWithDetails(phone)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public ApplicationDTO updateApplicationStatus(Integer id, Application.Status status, String reviewNotes) {
        Optional<Application> optionalApplication = applicationRepository.findById(id);
        if (optionalApplication.isPresent()) {
            Application application = optionalApplication.get();
            application.setStatus(status);
            application.setReviewTime(LocalDateTime.now());
            application.setReviewNotes(reviewNotes);
            Application updatedApplication = applicationRepository.save(application);
            return applicationRepository.findByIdWithDetails(id)
                    .map(this::convertToDTO)
                    .orElse(null);
        }
        return null;
    }

    @Override
    public void deleteApplication(Integer id) {
        applicationRepository.deleteById(id);
    }

    @Override
    public List<ApplicationDTO> findBySeekerPhoneAndStatus(String phone, Application.Status status) {
        return applicationRepository.findBySeekerPhoneAndStatus(phone, status)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<ApplicationDTO> findByRecruiterPhone(String recruiterPhone) {
        return applicationRepository.findByJobPositionRecruiterPhoneWithDetails(recruiterPhone)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<ApplicationDTO> findApplicationById(Integer id) {
        return applicationRepository.findByIdWithDetails(id)
                .map(this::convertToDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsApplication(String phone, Integer jobId) {
        return applicationRepository.existsBySeekerPhoneAndJobPositionId(phone, jobId);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<ApplicationDTO> findByStudentPhoneAndJobId(String studentPhone, Integer jobId) {
        return applicationRepository.findBySeekerPhoneAndJobPositionId(studentPhone, jobId)
                .map(this::convertToDTO);
    }
    @Override
    @Transactional
    public ApplicationDTO updateApplication(Integer id, ApplicationDTO applicationDTO) {
        // 查找现有的申请记录
        Application existingApplication = applicationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("申请记录不存在"));

        // 更新申请信息
        existingApplication.setJobIntention(applicationDTO.getJobIntention());
        existingApplication.setExpectedSalary(applicationDTO.getExpectedSalary());
        System.out.println("简历"+applicationDTO.getResumeFile().isEmpty());

        // 如果提供了新的简历文件，则更新
        if (applicationDTO.getResumeFile() != null && !applicationDTO.getResumeFile().isEmpty()) {
            try {
                existingApplication.setResumeFile(Base64.getDecoder().decode(applicationDTO.getResumeFile()));
            } catch (IllegalArgumentException e) {
                // 尝试使用URL安全Base64解码器
                existingApplication.setResumeFile(Base64.getUrlDecoder().decode(applicationDTO.getResumeFile()));
            }
        }
        existingApplication.setStatus(applicationDTO.getStatus());
        existingApplication.setReviewTime(null);
        existingApplication.setReviewNotes(null);

        existingApplication.setApplicationTime(applicationDTO.getApplicationTime());

        // 保存更新后的申请记录
        Application updatedApplication = applicationRepository.save(existingApplication);

        // 转换为DTO并返回
        return convertToDTO(updatedApplication);
    }

}
