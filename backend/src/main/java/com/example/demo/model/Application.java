package com.example.demo.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Table(name = "application")
public class Application  implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id; // 申请ID

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "job_id", nullable = false)
    private JobPosition jobPosition;

    @ManyToOne
    @JoinColumn(name = "student_phone", referencedColumnName = "phone", nullable = false)
    private Seeker seeker;

    @Column(name = "job_intention")
    private String jobIntention;
    @Column(name = "expected_salary",length = 100)
    private String expectedSalary;
    @Lob
    private byte[] resumeFile; // 简历文件(存储PDF/DOC等格式)

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private Status status; // 申请状态

    @Column(name = "application_time")
    private LocalDateTime applicationTime; // 申请时间

    @Column(name = "review_time")
    private LocalDateTime reviewTime; // 审核时间

    @Column(name = "review_notes")
    private String reviewNotes; // 审核备注

    // JPQL 枚举类型
    public enum Status{
        已投递, 已通过, 已拒绝;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public JobPosition getJobPosition() {
        return jobPosition;
    }

    public void setJobPosition(JobPosition jobPosition) {
        this.jobPosition = jobPosition;
    }

    public Seeker getSeeker() {
        return seeker;
    }

    public void setSeeker(Seeker seeker) {
        this.seeker = seeker;
    }

    public String getJobIntention() {
        return jobIntention;
    }

    public void setJobIntention(String jobIntention) {
        this.jobIntention = jobIntention;
    }

    public String getExpectedSalary() {
        return expectedSalary;
    }

    public void setExpectedSalary(String expectedSalary) {
        this.expectedSalary = expectedSalary;
    }

    public byte[] getResumeFile() {
        return resumeFile;
    }

    public void setResumeFile(byte[] resumeFile) {
        this.resumeFile = resumeFile;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public LocalDateTime getApplicationTime() {
        return applicationTime;
    }

    public void setApplicationTime(LocalDateTime applicationTime) {
        this.applicationTime = applicationTime;
    }

    public LocalDateTime getReviewTime() {
        return reviewTime;
    }

    public void setReviewTime(LocalDateTime reviewTime) {
        this.reviewTime = reviewTime;
    }

    public String getReviewNotes() {
        return reviewNotes;
    }

    public void setReviewNotes(String reviewNotes) {
        this.reviewNotes = reviewNotes;
    }

    public Application() {
    }

    public Application(JobPosition jobPosition, Seeker seeker, byte[] resumeFile) {
        this.jobPosition = jobPosition;
        this.seeker = seeker;
        this.resumeFile = resumeFile;
    }
}
