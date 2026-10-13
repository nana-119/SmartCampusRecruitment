package com.example.demo.controller;

import com.example.demo.dto.SeekerDTO;
import com.example.demo.model.Seeker;
import com.example.demo.service.SeekerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/seekers")
public class SeekerController {
    @Autowired
    private SeekerService seekerService;

    @GetMapping
    public ResponseEntity<List<SeekerDTO>> getAllSeekers() {
        List<SeekerDTO> seekers = seekerService.getAllSeekers();
        return ResponseEntity.ok(seekers);
    }
    @GetMapping("/findByPhone")
    public ResponseEntity<SeekerDTO> getSeekerByPhone(@RequestParam String phone) {
        SeekerDTO seeker = seekerService.findByPhone(phone);
        return ResponseEntity.ok(seeker);
    }

    @PostMapping
    public ResponseEntity<SeekerDTO> createSeeker(@RequestBody SeekerDTO seekerDTO) {
       SeekerDTO createdSeeker = seekerService.createSeeker(seekerDTO);
       return ResponseEntity.ok(createdSeeker);
    }

    @PutMapping("/{phone}")
    public ResponseEntity<SeekerDTO> updateSeeker(@PathVariable String phone, @RequestBody SeekerDTO seekerDTO) {
        System.out.println("收到解绑请求：phone={}, studentId={}"+phone+"      "+seekerDTO.getStudentId()); // 关键日志
        SeekerDTO updatedSeeker = seekerService.update(phone, seekerDTO);
        if (updatedSeeker != null)
            return ResponseEntity.ok(updatedSeeker);
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{phone}")
    public ResponseEntity<Void> deleteSeeker(@PathVariable String phone) {
        seekerService.delete(phone);
        return ResponseEntity.noContent().build();
    }
    @PutMapping("/resume/{phone}")
    public ResponseEntity<SeekerDTO> updateSeekerResume(@PathVariable String phone, @RequestBody SeekerDTO seekerDTO) {
        SeekerDTO updatedSeeker = seekerService.updateResumeByPhone(phone, seekerDTO.getResumeFile());
        if (updatedSeeker != null)
            return ResponseEntity.ok(updatedSeeker);
        return ResponseEntity.notFound().build();
    }
}
