package com.example.smartcampusrecruitment.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;

public class AcademicRecordDTO {
    @JsonProperty("graduationSchool")
    private String graduationSchool;
    @JsonProperty("studentNumber")
    private String studentNumber;
    @JsonProperty("subjectName")
    private String subjectName;
    @JsonProperty("score")
    private BigDecimal score;
    @JsonProperty("credit")
    private BigDecimal credit;
    @JsonProperty("academicYear")
    private String academicYear;
    @JsonProperty("semester")
    private String semester;

    public String getGraduationSchool() {
        return graduationSchool;
    }

    public void setGraduationSchool(String graduationSchool) {
        this.graduationSchool = graduationSchool;
    }

    public String getStudentNumber() {
        return studentNumber;
    }

    public void setStudentNumber(String studentNumber) {
        this.studentNumber = studentNumber;
    }

    public String getSubjectName() {
        return subjectName;
    }

    public void setSubjectName(String subjectName) {
        this.subjectName = subjectName;
    }

    public BigDecimal getScore() {
        return score;
    }

    public void setScore(BigDecimal score) {
        this.score = score;
    }

    public BigDecimal getCredit() {
        return credit;
    }

    public void setCredit(BigDecimal credit) {
        this.credit = credit;
    }

    public String getAcademicYear() {
        return academicYear;
    }

    public void setAcademicYear(String academicYear) {
        this.academicYear = academicYear;
    }

    public String getSemester() {
        return semester;
    }

    public void setSemester(String semester) {
        this.semester = semester;
    }
}
