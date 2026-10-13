package com.example.demo.service;

import com.example.demo.dto.JobPositionDTO;
import com.example.demo.dto.PositionStatDTO;
import com.example.demo.model.JobPosition;
import com.example.demo.model.Recruiter;
import com.example.demo.repository.ApplicationRepository;
import com.example.demo.repository.JobPositionRepository;
import com.example.demo.repository.RecruiterRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class JobPositionServiceImpl implements JobPositionService {
    private JobPositionRepository jobPositionRepository;
    private RecruiterRepository recruiterRepository;
    private ApplicationRepository applicationRepository;
    public JobPositionServiceImpl(JobPositionRepository jobPositionRepository,RecruiterRepository recruiterRepository,ApplicationRepository applicationRepository) {
        this.jobPositionRepository = jobPositionRepository;
        this.recruiterRepository = recruiterRepository;
        this.applicationRepository = applicationRepository;
    }

    @Override
    @Transactional
    public List<JobPositionDTO> getAllJobPositions() {
        return jobPositionRepository.findAll().stream()
                .map(JobPositionDTO::new)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public JobPositionDTO getJobPositionById(int id) {
        JobPosition jobPosition = jobPositionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("岗位不存在"));
        return new JobPositionDTO(jobPosition);
    }

    @Override
    @Transactional
    public JobPositionDTO createJobPosition(JobPositionDTO jobPositionDTO) {
        JobPosition jobPosition = new JobPosition();
        jobPosition.setTitle(jobPositionDTO.getTitle());
        jobPosition.setSalary(jobPositionDTO.getSalary());
        jobPosition.setDescription(jobPositionDTO.getDescription());
        jobPosition.setBenefits(jobPositionDTO.getBenefits());
        jobPosition.setWorkLocation(jobPositionDTO.getWorkLocation());
        jobPosition.setEducationRequirement(jobPositionDTO.getEducationRequirement());
        jobPosition.setExperienceRequirement(jobPositionDTO.getExperienceRequirement());
        jobPosition.setStatus(jobPositionDTO.getStatus());

        // 根据recruiterPhone查找Recruiter实体
        if (jobPositionDTO.getRecruiterPhone() != null) {
            Recruiter recruiter = recruiterRepository.findByPhone(jobPositionDTO.getRecruiterPhone())
                    .orElseThrow(() -> new RuntimeException("招聘者不存在"));
            jobPosition.setRecruiter(recruiter);
        }

        JobPosition savedJobPosition = jobPositionRepository.save(jobPosition);
        return new JobPositionDTO(savedJobPosition);
    }

    @Override
    @Transactional
    public JobPositionDTO updateJobPosition(int id, JobPositionDTO jobPositionDTO) {
        JobPosition jobPosition = jobPositionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("岗位不存在"));

        jobPosition.setTitle(jobPositionDTO.getTitle());
        jobPosition.setSalary(jobPositionDTO.getSalary());
        jobPosition.setDescription(jobPositionDTO.getDescription());
        jobPosition.setBenefits(jobPositionDTO.getBenefits());
        jobPosition.setWorkLocation(jobPositionDTO.getWorkLocation());
        jobPosition.setEducationRequirement(jobPositionDTO.getEducationRequirement());
        jobPosition.setExperienceRequirement(jobPositionDTO.getExperienceRequirement());
        jobPosition.setStatus(jobPositionDTO.getStatus());

        // 根据recruiterPhone查找Recruiter实体
        if (jobPositionDTO.getRecruiterPhone() != null) {
            Recruiter recruiter = recruiterRepository.findByPhone(jobPositionDTO.getRecruiterPhone())
                    .orElseThrow(() -> new RuntimeException("招聘者不存在"));
            jobPosition.setRecruiter(recruiter);
        }

        JobPosition updatedJobPosition = jobPositionRepository.save(jobPosition);
        return new JobPositionDTO(updatedJobPosition);
    }

    @Override
    @Transactional
    public void deleteJobPosition(int id) {
        JobPosition jobPosition = jobPositionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("岗位不存在"));
        jobPositionRepository.delete(jobPosition);
    }
    @Override
    @Transactional
    public List<JobPositionDTO> getJobPositionsByRecruiterPhoneAndStatus(String recruiterPhone, JobPosition.Status status) {
        return jobPositionRepository.findByRecruiter_PhoneAndStatus(recruiterPhone, status).stream()
                .map(JobPositionDTO::new)
                .collect(Collectors.toList());
    }
    @Override
    @Transactional
    public List<JobPositionDTO> getJobPositionsByRecruiterPhone(String recruiterPhone) {
        return jobPositionRepository.findByRecruiter_Phone(recruiterPhone).stream()
                .map(JobPositionDTO::new)
                .collect(Collectors.toList());
    }
    @Override
    @Transactional
    public List<JobPositionDTO> getJobPositionsByStatus(JobPosition.Status status) {
        return jobPositionRepository.findAll().stream()
                .filter(jobPosition -> jobPosition.getStatus() == status)
                .map(JobPositionDTO::new)
                .collect(Collectors.toList());
    }


    @Override
    @Transactional
    public List<PositionStatDTO> getPublishTop10() {
        // 1. 查询所有岗位
        List<JobPosition> positions = jobPositionRepository.findAll();

        // 2. 按岗位名称分组统计发布数量
        Map<String, Long> publishCountMap = positions.stream()
                .collect(Collectors.groupingBy(
                        JobPosition::getTitle, // 按岗位名称（title）分组
                        Collectors.counting()  // 统计每组数量
                ));

        // 3. 转换为DTO并按数量降序取前10
        return publishCountMap.entrySet().stream()
                .map(entry -> new PositionStatDTO(entry.getKey(), entry.getValue().intValue()))
                .sorted((d1, d2) -> Integer.compare(d2.getCount(), d1.getCount())) // 降序排序
                .limit(10) // 取前10
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public List<PositionStatDTO> getDeliveryTop10() {
        List<Object[]> deliveryStats = applicationRepository.countByPositionGroup();

        return deliveryStats.stream()
                .map(stat -> {
                    String positionName = (String) stat[0]; // 岗位名称
                    int deliveryCount = ((Number) stat[1]).intValue(); // 投递次数
                    return new PositionStatDTO(positionName, deliveryCount);
                })
                .sorted((d1, d2) -> Integer.compare(d2.getCount(), d1.getCount()))
                .limit(10)
                .collect(Collectors.toList());
    }
}
