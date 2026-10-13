package com.example.demo.dto;

import com.example.demo.model.EducationItem;
import com.example.demo.model.ExperienceItem;

import java.util.List;

public class ResumeDTO {
    private String name;
    private String email;
    private String phone;
    private String skills;
    private Integer template;
    private String zhaopian;
    private List<EducationItem> educations;
    private List<ExperienceItem> experiences;

    public ResumeDTO() {
    }

    public ResumeDTO(String name, String email, String phone, String skills, Integer template, String zhaopian, List<EducationItem> educations, List<ExperienceItem> experiences) {
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.skills = skills;
        this.template = template;
        this.zhaopian = zhaopian;
        this.educations = educations;
        this.experiences = experiences;
    }

    // Getter and Setter methods
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getSkills() {
        return skills;
    }

    public void setSkills(String skills) {
        this.skills = skills;
    }

    public Integer getTemplate() {
        return template;
    }

    public void setTemplate(Integer template) {
        this.template = template;
    }

    public String getZhaopian() {
        return zhaopian;
    }

    public void setZhaopian(String zhaopian) {
        this.zhaopian = zhaopian;
    }

    public List<EducationItem> getEducations() {
        return educations;
    }

    public void setEducations(List<EducationItem> educations) {
        this.educations = educations;
    }

    public List<ExperienceItem> getExperiences() {
        return experiences;
    }

    public void setExperiences(List<ExperienceItem> experiences) {
        this.experiences = experiences;
    }
}
