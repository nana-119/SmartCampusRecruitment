package com.example.demo.service;

import com.example.demo.dto.JobPositionDTO;
import com.example.demo.model.Application;
import com.example.demo.model.JobPosition;
import com.example.demo.model.Seeker;
import com.example.demo.repository.ApplicationRepository;
import com.example.demo.repository.JobPositionRepository;
import com.example.demo.repository.SeekerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class RecommendationServiceImpl  implements RecommendationService{
    @Autowired
    private ApplicationRepository applicationRepository;

    @Autowired
    private JobPositionRepository jobPositionRepository;

    @Autowired
    private SeekerRepository seekerRepository;
    @Autowired
    private InterestSimilarityService interestSimilarityService;

    private static final double BEHAVIOR_WEIGHT = 0.6;
    private static final double INTEREST_WEIGHT = 0.4;
    private static final double MIN_SIMILARITY = 0.1;

    @Override
    //目标用户有相似求职偏好和行为的其他求职者
    public List<JobPositionDTO> recommendJobsBasedOnUsers(String seekerPhone, int count) {
        // 获取目标用户
        Seeker targetSeeker = seekerRepository.findByPhone(seekerPhone)
                .orElseThrow(() -> new RuntimeException("用户不存在"));

        // 获取所有用户的行为数据
        List<Application> allApplications = applicationRepository.findAll();

        // 构建用户-岗位评分矩阵
        Map<Integer, Map<Integer, Double>> userJobMatrix = buildUserJobMatrix(allApplications);

        // 计算全局平均评分
        double globalAverage = calculateGlobalAverage(userJobMatrix);

        // 如果目标用户没有行为数据，则使用基于兴趣的推荐或热门推荐
        if (!userJobMatrix.containsKey(targetSeeker.getId()) ||
                userJobMatrix.get(targetSeeker.getId()).isEmpty()) {
            System.out.println("使用基于兴趣的热门推荐");
            return recommendByInterestsOrPopular(targetSeeker, count);
        }

        // 计算用户相似度（使用皮尔逊相关系数）
        Map<Integer, Double> similarities = calculateEnhancedUserSimilarities(
                userJobMatrix, targetSeeker.getId(), globalAverage);

        // 预测评分（加入用户偏差）
        Map<Integer, Double> predictedRatings = predictRatingsWithBias(userJobMatrix, similarities, targetSeeker.getId(), globalAverage);

        // 获取已申请或已查看的岗位ID，避免重复推荐
        Set<Integer> appliedJobIds = allApplications.stream()
                .filter(app -> app.getSeeker().getId().equals(targetSeeker.getId()))
                .map(app -> app.getJobPosition().getId())
                .collect(Collectors.toSet());

        // 排序并返回推荐结果
        return predictedRatings.entrySet().stream()
                .filter(entry -> !appliedJobIds.contains(entry.getKey())) // 过滤已申请的岗位
                .sorted(Map.Entry.<Integer, Double>comparingByValue().reversed())
                .limit(count)
                .map(entry -> jobPositionRepository.findById(entry.getKey())
                        .filter(job -> job.getStatus() == JobPosition.Status.正在招聘) // 提前过滤
                        .map(JobPositionDTO::new)
                        .orElse(null))
                .collect(Collectors.toList());
    }

    @Override
    public List<JobPositionDTO> recommendJobsBasedOnItems(String seekerPhone, int count) {
        // 获取目标用户
        Seeker targetSeeker = seekerRepository.findByPhone(seekerPhone)
                .orElseThrow(() -> new RuntimeException("用户不存在"));

        // 获取所有用户的行为数据
        List<Application> allApplications = applicationRepository.findAll();

        // 构建用户-岗位评分矩阵
        Map<Integer, Map<Integer, Double>> userJobMatrix = buildUserJobMatrix(allApplications);

        // 计算全局平均评分
        double globalAverage = calculateGlobalAverage(userJobMatrix);

        // 如果目标用户没有行为数据，则使用热门推荐
        if (!userJobMatrix.containsKey(targetSeeker.getId()) ||
                userJobMatrix.get(targetSeeker.getId()).isEmpty()) {
            return getPopularJobs(count);
        }

        // 获取用户已交互的岗位
        Set<Integer> userJobIds = userJobMatrix.get(targetSeeker.getId()).keySet();

        // 构建岗位-岗位相似度矩阵
        Map<Integer, Map<Integer, Double>> jobSimilarities = calculateJobSimilarities(allApplications);

        // 计算每个岗位的预测评分（加入偏差调整）
        Map<Integer, Double> jobScores = new HashMap<>();
        List<JobPosition> allJobs = jobPositionRepository.findAll().stream()
                .filter(job -> job.getStatus() == JobPosition.Status.正在招聘)
                .collect(Collectors.toList());

        // 计算目标用户的平均评分
        double targetUserAverage = userJobMatrix.get(targetSeeker.getId()).values().stream()
                .mapToDouble(Double::doubleValue)
                .average()
                .orElse(globalAverage);

        for (JobPosition job : allJobs) {
            Integer jobId = job.getId();
            // 跳过用户已经交互过的岗位
            if (userJobIds.contains(jobId)) {
                continue;
            }

            double score = 0.0;
            double similaritySum = 0.0;
            double weightedDeviationSum = 0.0;

            // 基于用户已交互的岗位计算相似度
            for (Integer userJobId : userJobIds) {
                if (jobSimilarities.containsKey(jobId) &&
                        jobSimilarities.get(jobId).containsKey(userJobId)) {
                    double similarity = jobSimilarities.get(jobId).get(userJobId);

                    // 找到对userJobId评分的其他用户，计算他们的平均评分偏差
                    for (Map.Entry<Integer, Map<Integer, Double>> userEntry : userJobMatrix.entrySet()) {
                        Integer otherUserId = userEntry.getKey();
                        Map<Integer, Double> otherUserRatings = userEntry.getValue();

                        if (otherUserId.equals(targetSeeker.getId())) {
                            continue;
                        }

                        if (otherUserRatings.containsKey(userJobId)) {
                            double otherUserAverage = otherUserRatings.values().stream()
                                    .mapToDouble(Double::doubleValue)
                                    .average()
                                    .orElse(globalAverage);

                            double rating = otherUserRatings.get(userJobId);
                            double deviation = rating - otherUserAverage;

                            weightedDeviationSum += similarity * deviation;
                            similaritySum += Math.abs(similarity);
                        }
                    }
                }
            }

            if (similaritySum > 0) {
                double predictedRating = targetUserAverage + (weightedDeviationSum / similaritySum);
                // 确保评分在合理范围内
                predictedRating = Math.max(1.0, Math.min(5.0, predictedRating));
                jobScores.put(jobId, predictedRating);
            } else {
                // 如果没有足够的相似度信息，使用全局平均值
                jobScores.put(jobId, globalAverage);
            }
        }

        // 获取已申请或已查看的岗位ID，避免重复推荐
        Set<Integer> appliedJobIds = allApplications.stream()
                .filter(app -> app.getSeeker().getId().equals(targetSeeker.getId()))
                .map(app -> app.getJobPosition().getId())
                .collect(Collectors.toSet());

        // 返回评分最高的岗位
        return jobScores.entrySet().stream()
                .filter(entry -> !appliedJobIds.contains(entry.getKey()))
                .sorted(Map.Entry.<Integer, Double>comparingByValue().reversed())
                .limit(count)
                .map(entry -> jobPositionRepository.findById(entry.getKey())
                        .map(JobPositionDTO::new)
                        .orElse(null))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }
    /**
     * 构建用户-岗位评分矩阵
     */
    private Map<Integer, Map<Integer, Double>> buildUserJobMatrix(List<Application> applications) {
        Map<Integer, Map<Integer, Double>> userJobMatrix = new HashMap<>();

        for (Application app : applications) {
            Integer userId = app.getSeeker().getId();
            Integer jobId = app.getJobPosition().getId();
            Double rating = getRatingFromStatus(app.getStatus());

            userJobMatrix.computeIfAbsent(userId, k -> new HashMap<>());
            userJobMatrix.get(userId).put(jobId, rating);
        }

        return userJobMatrix;
    }

    /**
     * 根据状态获取评分
     */
    private Double getRatingFromStatus(Application.Status status) {
        switch (status) {
            case 已投递: return 5.0;
            case 已通过: return 4.0;
            case 已拒绝: return 2.0;
            default: return 1.0;
        }
    }

    /**
     * 计算皮尔逊相关系数
     */
    private double calculatePearsonCorrelation(Map<Integer, Double> user1, Map<Integer, Double> user2) {
        double sumUser1 = 0.0, sumUser2 = 0.0, sumUser1User2 = 0.0, sumUser1Squared = 0.0, sumUser2Squared = 0.0;
        int commonItems = 0;

        for (Map.Entry<Integer, Double> entry : user1.entrySet()) {
            if (user2.containsKey(entry.getKey())) {
                double rating1 = entry.getValue();
                double rating2 = user2.get(entry.getKey());

                sumUser1 += rating1;
                sumUser2 += rating2;
                sumUser1User2 += rating1 * rating2;
                sumUser1Squared += rating1 * rating1;
                sumUser2Squared += rating2 * rating2;
                commonItems++;
            }
        }

        if (commonItems == 0) {
            return 0.0;
        }

        double numerator = sumUser1User2 - (sumUser1 * sumUser2 / commonItems);
        double denominator = Math.sqrt((sumUser1Squared - Math.pow(sumUser1, 2) / commonItems) *
                (sumUser2Squared - Math.pow(sumUser2, 2) / commonItems));

        return denominator == 0 ? 0.0 : numerator / denominator;
    }

    /**
     * 计算用户相似度（使用皮尔逊相关系数）
     */
    private Map<Integer, Double> calculateEnhancedUserSimilarities(
            Map<Integer, Map<Integer, Double>> userJobMatrix,
            Integer targetUserId,
            double globalAverage) {

        Map<Integer, Double> similarities = new HashMap<>();
        Map<Integer, Double> targetUserRatings = userJobMatrix.get(targetUserId);

        if (targetUserRatings == null) {
            return similarities;
        }

        // 获取目标用户的兴趣爱好
        Seeker targetSeeker = seekerRepository.findById(targetUserId).orElse(null);
        Set<String> targetHobbies = targetSeeker != null ?
                interestSimilarityService.parseHobbies(targetSeeker.getHobbies()) :
                Collections.emptySet();

        for (Map.Entry<Integer, Map<Integer, Double>> entry : userJobMatrix.entrySet()) {
            Integer otherUserId = entry.getKey();

            // 跳过目标用户自身
            if (otherUserId.equals(targetUserId)) {
                continue;
            }

            Map<Integer, Double> otherUserRatings = entry.getValue();

            // 1. 计算行为相似度（皮尔逊）
            double behaviorSimilarity = calculatePearsonCorrelation(targetUserRatings, otherUserRatings);

            // 2. 计算兴趣相似度
            Seeker otherSeeker = seekerRepository.findById(otherUserId).orElse(null);
            Set<String> otherHobbies = otherSeeker != null ?
                    interestSimilarityService.parseHobbies(otherSeeker.getHobbies()) :
                    Collections.emptySet();

            double interestSimilarity = interestSimilarityService.calculateJaccardSimilarity(
                    targetHobbies, otherHobbies);

            // 3. 动态调整权重（如果共同评价项目太少，增加兴趣权重）
            int commonItems = countCommonItems(targetUserRatings, otherUserRatings);
            double dynamicBehaviorWeight = BEHAVIOR_WEIGHT;
            double dynamicInterestWeight = INTEREST_WEIGHT;

            if (commonItems < 2) { // 共同评价少于2个
                dynamicBehaviorWeight = 0.3;
                dynamicInterestWeight = 0.7;
            }

            // 4. 融合两种相似度
            double combinedSimilarity = (behaviorSimilarity * dynamicBehaviorWeight) +
                    (interestSimilarity * dynamicInterestWeight);

            // 过滤低相似度的用户
            if (combinedSimilarity >= MIN_SIMILARITY) {
                similarities.put(otherUserId, combinedSimilarity);
            }
        }

        return similarities;
    }

    /**
     * 计算共同评价项目数量
     */
    private int countCommonItems(Map<Integer, Double> ratings1, Map<Integer, Double> ratings2) {
        int count = 0;
        for (Integer jobId : ratings1.keySet()) {
            if (ratings2.containsKey(jobId)) {
                count++;
            }
        }
        return count;
    }

    /**
     * 预测评分（加入偏差调整）
     */
    private Map<Integer, Double> predictRatingsWithBias(
            Map<Integer, Map<Integer, Double>> userJobMatrix,
            Map<Integer, Double> similarities,
            Integer targetUserId,
            double globalAverage) {

        Map<Integer, Double> predictions = new HashMap<>();

        // 计算目标用户的平均评分
        double userAverage = userJobMatrix.get(targetUserId).values().stream()
                .mapToDouble(Double::doubleValue)
                .average()
                .orElse(globalAverage);

        // 获取目标用户已评分的岗位
        Set<Integer> ratedJobs = new HashSet<>();
        if (userJobMatrix.containsKey(targetUserId)) {
            ratedJobs = userJobMatrix.get(targetUserId).keySet();
        }

        // 对于每个岗位，计算预测评分
        for (Map.Entry<Integer, Map<Integer, Double>> userEntry : userJobMatrix.entrySet()) {
            Integer userId = userEntry.getKey();

            // 跳过目标用户自身
            if (userId.equals(targetUserId)) {
                continue;
            }

            Map<Integer, Double> userRatings = userEntry.getValue();

            // 计算其他用户的平均评分
            double otherUserAverage = userRatings.values().stream()
                    .mapToDouble(Double::doubleValue)
                    .average()
                    .orElse(globalAverage);

            for (Map.Entry<Integer, Double> jobEntry : userRatings.entrySet()) {
                Integer jobId = jobEntry.getKey();

                // 跳过用户已经评分的岗位
                if (ratedJobs.contains(jobId)) {
                    continue;
                }

                Double similarity = similarities.get(userId);
                Double rating = jobEntry.getValue();

                if (similarity != null) {
                    // 使用偏差调整的评分
                    predictions.merge(jobId, similarity * (rating - otherUserAverage), Double::sum);
                }
            }
        }

        // 归一化预测评分并加上用户平均值
        for (Map.Entry<Integer, Double> entry : predictions.entrySet()) {
            Integer jobId = entry.getKey();
            Double score = entry.getValue();

            double similaritySum = similarities.values().stream()
                    .mapToDouble(Double::doubleValue)
                    .sum();

            if (Math.abs(similaritySum) > 1e-10) { // 避免除零错误
                double predictedRating = userAverage + (score / similaritySum);
                // 确保评分在合理范围内 (1-5)
                predictedRating = Math.max(1.0, Math.min(5.0, predictedRating));
                predictions.put(jobId, predictedRating);
            } else {
                // 如果相似度总和接近零，使用全局平均值
                predictions.put(jobId, globalAverage);
            }
        }

        return predictions;
    }

    /**
     * 计算全局平均评分
     */
    private double calculateGlobalAverage(Map<Integer, Map<Integer, Double>> userJobMatrix) {
        double sum = 0.0;
        int count = 0;

        for (Map<Integer, Double> ratings : userJobMatrix.values()) {
            for (Double rating : ratings.values()) {
                sum += rating;
                count++;
            }
        }

        return count > 0 ? sum / count : 3.0; // 默认返回中等评分3.0
    }

    /**
     * 计算岗位之间的相似度
     */
    private Map<Integer, Map<Integer, Double>> calculateJobSimilarities(List<Application> applications) {
        // 构建岗位-用户倒排矩阵
        Map<Integer, Set<Integer>> jobUsers = new HashMap<>();

        for (Application app : applications) {
            Integer jobId = app.getJobPosition().getId();
            Integer userId = app.getSeeker().getId();

            jobUsers.computeIfAbsent(jobId, k -> new HashSet<>());
            jobUsers.get(jobId).add(userId);
        }

        // 计算岗位相似度
        Map<Integer, Map<Integer, Double>> jobSimilarities = new HashMap<>();

        for (Map.Entry<Integer, Set<Integer>> entry1 : jobUsers.entrySet()) {
            Integer jobId1 = entry1.getKey();
            Set<Integer> users1 = entry1.getValue();

            for (Map.Entry<Integer, Set<Integer>> entry2 : jobUsers.entrySet()) {
                Integer jobId2 = entry2.getKey();

                if (jobId1.equals(jobId2)) {
                    continue;
                }

                Set<Integer> users2 = entry2.getValue();

                // 计算Jaccard相似度
                Set<Integer> intersection = new HashSet<>(users1);
                intersection.retainAll(users2);

                Set<Integer> union = new HashSet<>(users1);
                union.addAll(users2);

                double similarity = 0.0;
                if (!union.isEmpty()) {
                    similarity = (double) intersection.size() / union.size();
                }

                jobSimilarities.computeIfAbsent(jobId1, k -> new HashMap<>());
                jobSimilarities.get(jobId1).put(jobId2, similarity);
            }
        }

        return jobSimilarities;
    }

    /**
     * 基于兴趣的推荐或热门推荐（用于冷启动）
     */
    private List<JobPositionDTO> recommendByInterestsOrPopular(Seeker targetSeeker, int count) {
        Set<String> targetHobbies = interestSimilarityService.parseHobbies(targetSeeker.getHobbies());

        // 如果没有兴趣爱好，直接返回热门推荐
        if (targetHobbies.isEmpty()) {
            return getPopularJobs(count);
        }

        // 基于兴趣推荐：找到有相似兴趣的用户喜欢的岗位
        List<Seeker> allSeekers = seekerRepository.findAll();
        Map<Integer, Double> interestSimilarities = new HashMap<>();

        for (Seeker otherSeeker : allSeekers) {
            if (otherSeeker.getId().equals(targetSeeker.getId())) {
                continue;
            }

            Set<String> otherHobbies = interestSimilarityService.parseHobbies(otherSeeker.getHobbies());
            double similarity = interestSimilarityService.calculateJaccardSimilarity(
                    targetHobbies, otherHobbies);

            if (similarity > 0.3) { // 兴趣相似度阈值
                interestSimilarities.put(otherSeeker.getId(), similarity);
            }
        }

        // 如果找到相似兴趣用户，获取他们喜欢的岗位
        if (!interestSimilarities.isEmpty()) {
            return getJobsFromSimilarUsers(interestSimilarities, count);
        }

        // 如果没有找到相似兴趣用户，返回热门推荐
        return getPopularJobs(count);
    }

    /**
     * 从相似用户获取推荐的岗位
     */
    private List<JobPositionDTO> getJobsFromSimilarUsers(Map<Integer, Double> userSimilarities, int count) {
        List<Application> allApplications = applicationRepository.findAll();
        Map<Integer, Map<Integer, Double>> userJobMatrix = buildUserJobMatrix(allApplications);

        // 统计相似用户喜欢的岗位
        Map<Integer, Double> jobScores = new HashMap<>();

        for (Map.Entry<Integer, Double> entry : userSimilarities.entrySet()) {
            Integer userId = entry.getKey();
            Double similarity = entry.getValue();

            if (userJobMatrix.containsKey(userId)) {
                for (Map.Entry<Integer, Double> jobEntry : userJobMatrix.get(userId).entrySet()) {
                    Integer jobId = jobEntry.getKey();
                    Double rating = jobEntry.getValue();

                    // 加权评分
                    jobScores.merge(jobId, similarity * rating, Double::sum);
                }
            }
        }

        // 返回评分最高的岗位
        return jobScores.entrySet().stream()
                .sorted(Map.Entry.<Integer, Double>comparingByValue().reversed())
                .limit(count)
                .map(entry -> jobPositionRepository.findById(entry.getKey())
                        .filter(job -> job.getStatus() == JobPosition.Status.正在招聘)
                        .map(JobPositionDTO::new)
                        .orElse(null))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    /**
     * 获取热门岗位（基于申请数量）
     */
    private List<JobPositionDTO> getPopularJobs(int count) {
        List<Application> applications = applicationRepository.findAll();

        // 统计岗位申请数量
        Map<Integer, Long> jobApplicationCount = applications.stream()
                .filter(app -> app.getJobPosition().getStatus() == JobPosition.Status.正在招聘)
                .collect(Collectors.groupingBy(
                        app -> app.getJobPosition().getId(),
                        Collectors.counting()
                ));

        // 返回申请数量最多的岗位
        return jobApplicationCount.entrySet().stream()
                .sorted(Map.Entry.<Integer, Long>comparingByValue().reversed())
                .limit(count)
                .map(entry -> jobPositionRepository.findById(entry.getKey())
                        .map(JobPositionDTO::new)
                        .orElse(null))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

}
