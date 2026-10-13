package com.example.demo.controller;

import com.example.demo.dto.RecruiterDTO;
import com.example.demo.dto.SeekerDTO;
import com.example.demo.model.Recruiter;
import com.example.demo.service.RecruiterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/recruiters")
public class RecruiterController {
    @Autowired
    private RecruiterService recruiterService;

    @GetMapping
    public ResponseEntity<List<RecruiterDTO>>getRecruiters() {
        List<RecruiterDTO> recruiters = recruiterService.getAllRecruiters();
        return ResponseEntity.ok(recruiters);
    }
    @GetMapping("/findByPhone")
    public ResponseEntity<RecruiterDTO> findByPhone(@RequestParam String phone) {
        RecruiterDTO recruiter = recruiterService.findByPhone(phone);
        return ResponseEntity.ok(recruiter);
    }

    @PostMapping
    public ResponseEntity<RecruiterDTO> createRecruiter(@RequestBody RecruiterDTO recruiterDTO) {
        RecruiterDTO createdRecruiter = recruiterService.createRecruiter(recruiterDTO);
        return ResponseEntity.ok(createdRecruiter);
    }

    @PutMapping("/{phone}")
    public ResponseEntity<RecruiterDTO> updateRecruiter(@PathVariable String phone, @RequestBody RecruiterDTO recruiterDTO) {
        RecruiterDTO updatedRecruiter= recruiterService.update(phone, recruiterDTO);
        if (updatedRecruiter == null)
            return ResponseEntity.notFound().build();
        return ResponseEntity.ok(updatedRecruiter);
    }

    @DeleteMapping("/{phone}")
    public ResponseEntity<Void> deleteRecruiter(@PathVariable String phone) {
        recruiterService.delete(phone);
        return ResponseEntity.noContent().build();
    }

}
