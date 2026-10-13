package com.example.demo.service;

import com.example.demo.dto.JobPositionDTO;

import java.util.List;

public interface RecommendationService {
    /**
     * 基于用户的协同过滤推荐
     * @param seekerPhone 求职者手机号
     * @param count 推荐数量
     * @return 推荐的岗位列表
     */
    List<JobPositionDTO> recommendJobsBasedOnUsers(String seekerPhone, int count);

    /**
     * 基于物品的协同过滤推荐
     * @param seekerPhone 求职者手机号
     * @param count 推荐数量
     * @return 推荐的岗位列表
     */
    List<JobPositionDTO> recommendJobsBasedOnItems(String seekerPhone, int count);
}

