package com.example.demo.controller;

import com.example.demo.dto.JobPositionDTO;
import com.example.demo.model.JobPosition;
import com.example.demo.model.Recruiter;
import com.example.demo.repository.JobPositionRepository;
import com.example.demo.service.JobPositionService;
import com.example.demo.service.RecommendationService;
import com.example.demo.service.RecruiterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/jobpositions")
public class JobPositionController {
    @Autowired
    private JobPositionService jobPositionService;
    @Autowired
    private RecommendationService recommendationService;

    @GetMapping
    public ResponseEntity<List<JobPositionDTO>> getJobPositions() {
        List<JobPositionDTO> jobPositions = jobPositionService.getAllJobPositions();
        return ResponseEntity.ok(jobPositions);
    }
    @GetMapping("/{id}")
    public ResponseEntity<JobPositionDTO> getJobPositionById(@PathVariable int id) {
        try {
            JobPositionDTO jobPosition = jobPositionService.getJobPositionById(id);
            return ResponseEntity.ok(jobPosition);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping
    public ResponseEntity<JobPositionDTO> createJobPosition(@RequestBody JobPositionDTO jobPositionDTO) {
        try {
            JobPositionDTO createdJobPosition = jobPositionService.createJobPosition(jobPositionDTO);
            return ResponseEntity.ok(createdJobPosition);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<JobPositionDTO> updateJobPosition(@PathVariable int id, @RequestBody JobPositionDTO jobPositionDTO) {
        try {
            JobPositionDTO updatedJobPosition = jobPositionService.updateJobPosition(id, jobPositionDTO);
            return ResponseEntity.ok(updatedJobPosition);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteJobPosition(@PathVariable int id) {
        try {
            jobPositionService.deleteJobPosition(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    @GetMapping("/recruiter/{recruiterPhone}/status/{status}")
    public ResponseEntity<List<JobPositionDTO>> getJobPositionsByRecruiterPhoneAndStatus(
            @PathVariable String recruiterPhone,
            @PathVariable JobPosition.Status status) {
        try {
            List<JobPositionDTO> jobPositions = jobPositionService.getJobPositionsByRecruiterPhoneAndStatus(recruiterPhone, status);
            return ResponseEntity.ok(jobPositions);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/recruiter/{recruiterPhone}")
    public ResponseEntity<List<JobPositionDTO>> getJobPositionsByRecruiterPhone(@PathVariable String recruiterPhone) {
        try {
            List<JobPositionDTO> jobPositions = jobPositionService.getJobPositionsByRecruiterPhone(recruiterPhone);
            return ResponseEntity.ok(jobPositions);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    @GetMapping("/status/{status}")
    public ResponseEntity<List<JobPositionDTO>> getJobPositionsByStatus(@PathVariable JobPosition.Status status) {
        try {
            List<JobPositionDTO> jobPositions = jobPositionService.getJobPositionsByStatus(status);
            return ResponseEntity.ok(jobPositions);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // 添加基于用户的协同过滤推荐接口（改进版）
    @GetMapping("/recommendations/user-based/{seekerPhone}")
    public ResponseEntity<List<JobPositionDTO>> getUserBasedRecommendations(
            @PathVariable String seekerPhone,
            @RequestParam(defaultValue = "10") int count) {
        try {
            List<JobPositionDTO> recommendations = recommendationService.recommendJobsBasedOnUsers(seekerPhone, count);
            return ResponseEntity.ok(recommendations);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Collections.emptyList());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Collections.emptyList());
        }
    }

    // 添加基于物品的协同过滤推荐接口（改进版）
    @GetMapping("/recommendations/item-based/{seekerPhone}")
    public ResponseEntity<List<JobPositionDTO>> getItemBasedRecommendations(
            @PathVariable String seekerPhone,
            @RequestParam(defaultValue = "10") int count) {
        try {
            List<JobPositionDTO> recommendations = recommendationService.recommendJobsBasedOnItems(seekerPhone, count);
            return ResponseEntity.ok(recommendations);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Collections.emptyList());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Collections.emptyList());
        }
    }

}
