package com.example.smartcampusrecruitment.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;

public class JobPositionDTO implements Serializable {
    @JsonProperty("id")
    private Integer id;
    @JsonProperty("title")
    private String title;
    @JsonProperty("salary")
    private String salary;
    @JsonProperty("description")
    private String description;
    @JsonProperty("benefits")
    private String benefits;
    @JsonProperty("workLocation")
    private String workLocation;
    @JsonProperty("educationRequirement")
    private String educationRequirement;
    @JsonProperty("experienceRequirement")
    private String experienceRequirement;
    @JsonProperty("status")
    private Integer status;
    @JsonProperty("recruiterPhone")
    private String recruiterPhone;
    @JsonProperty("recruiterName")
    private String recruiterName;
    @JsonProperty("companyName")
    private String companyName;

    @JsonProperty("position")
    private String position;

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

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
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
