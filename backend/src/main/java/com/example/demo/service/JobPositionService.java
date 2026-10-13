package com.example.demo.service;

import com.example.demo.dto.JobPositionDTO;
import com.example.demo.dto.PositionStatDTO;
import com.example.demo.model.JobPosition;

import java.util.List;

public interface JobPositionService {
    List<JobPositionDTO> getAllJobPositions();
    JobPositionDTO getJobPositionById(int id);
    JobPositionDTO createJobPosition(JobPositionDTO jobPositionDTO);
    JobPositionDTO updateJobPosition(int id, JobPositionDTO jobPositionDTO);
    void deleteJobPosition(int id);
    // 在原有基础上添加新方法
    List<JobPositionDTO> getJobPositionsByRecruiterPhoneAndStatus(String recruiterPhone, JobPosition.Status status);
    List<JobPositionDTO> getJobPositionsByRecruiterPhone(String recruiterPhone);
    List<JobPositionDTO> getJobPositionsByStatus(JobPosition.Status status);
    List<PositionStatDTO> getPublishTop10();

    // 获取投递量Top10岗位（假设存在投递记录关联）
    List<PositionStatDTO> getDeliveryTop10();

}
