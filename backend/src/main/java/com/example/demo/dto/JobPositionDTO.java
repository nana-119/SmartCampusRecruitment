package com.example.demo.dto;

import com.example.demo.model.JobPosition;

public class JobPositionDTO {
    private Integer id;
    private String title;
    private String salary;
    private String description;
    private String benefits;
    private String workLocation;
    private String educationRequirement;
    private String experienceRequirement;
    private JobPosition.Status status;
    private String recruiterPhone;
    private String recruiterName;
    private String companyName;
    private String position;

    public JobPositionDTO() {
    }

    public JobPositionDTO(JobPosition jobPosition) {
        this.id = jobPosition.getId();
        this.title = jobPosition.getTitle();
        this.salary = jobPosition.getSalary();
        this.description = jobPosition.getDescription();
        this.benefits = jobPosition.getBenefits();
        this.workLocation = jobPosition.getWorkLocation();
        this.educationRequirement = jobPosition.getEducationRequirement();
        this.experienceRequirement = jobPosition.getExperienceRequirement();
        this.status = jobPosition.getStatus();
        
        if (jobPosition.getRecruiter() != null) {
            this.recruiterPhone = jobPosition.getRecruiter().getPhone();
            this.recruiterName = jobPosition.getRecruiter().getName();
            this.companyName = jobPosition.getRecruiter().getCompany();
            this.position = jobPosition.getRecruiter().getPosition();
        }
    }

    // Getters and Setters
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSalary() {
        return salary;
    }

    public void setSalary(String salary) {
        this.salary = salary;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getBenefits() {
        return benefits;
    }

    public void setBenefits(String benefits) {
        this.benefits = benefits;
    }

    public String getWorkLocation() {
        return workLocation;
    }

    public void setWorkLocation(String workLocation) {
        this.workLocation = workLocation;
    }

    public String getEducationRequirement() {
        return educationRequirement;
    }

    public void setEducationRequirement(String educationRequirement) {
        this.educationRequirement = educationRequirement;
    }

    public String getExperienceRequirement() {
        return experienceRequirement;
    }

    public void setExperienceRequirement(String experienceRequirement) {
        this.experienceRequirement = experienceRequirement;
    }

    public JobPosition.Status getStatus() {
        return status;
    }

    public void setStatus(JobPosition.Status status) {
        this.status = status;
    }

    public String getRecruiterPhone() {
        return recruiterPhone;
    }

    public void setRecruiterPhone(String recruiterPhone) {
        this.recruiterPhone = recruiterPhone;
    }

    public String getRecruiterName() {
        return recruiterName;
    }

    public void setRecruiterName(String recruiterName) {
        this.recruiterName = recruiterName;
    }
    public String getCompanyName() {
        return companyName;
    }
    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }
}
