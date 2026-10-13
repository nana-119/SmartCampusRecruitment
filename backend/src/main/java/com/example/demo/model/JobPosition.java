package com.example.demo.model;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

@Entity
@Table(name="job_position")
public class JobPosition implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    @Column(name = "id", nullable = false)
    private Integer id;

    @Column(name = "title", nullable = false, length = 100)
    private String title;

    @Column(name = "salary", nullable = false, length = 100)
    private String salary;

    @Lob
    @Column(name = "description", nullable = false)
    private String description;

    @Lob
    @Column(name = "benefits")
    private String benefits;

    @Column(name = "work_location", length = 100)
    private String workLocation;

    @Column(name = "education_requirement", length = 50)
    private String educationRequirement;

    @Column(name = "experience_requirement", length = 100)
    private String experienceRequirement;
    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private Status status;

    @ManyToOne
    @JoinColumn(name = "contact_phone",referencedColumnName = "phone",  nullable = false)
    private Recruiter recruiter;

    @OneToMany(mappedBy = "jobPosition", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Application> applications;

    public enum Status{
        待审核,审核不通过, 正在招聘, 已关闭;
    }

        public JobPosition() {
    }

    public JobPosition(String title, String salary, String description, Recruiter recruiter) {
        this.title = title;
        this.salary = salary;
        this.description = description;
        this.recruiter = recruiter;
    }

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

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public String getContactPhone() {
        return recruiter != null ? recruiter.getPhone() : null;
    }

    public List<Application> getApplications() {
        return applications;
    }

    public void setApplications(List<Application> applications) {
        this.applications = applications;
    }

    public Recruiter getRecruiter() {
        return recruiter;
    }

    public void setRecruiter(Recruiter recruiter) {
        this.recruiter = recruiter;
    }


}
