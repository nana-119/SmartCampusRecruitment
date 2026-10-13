package com.example.demo.model;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;

@Entity
@Table(name = "temp_academic_record")
@IdClass(AcademicRecordId.class)
public class TempAcademicRecord implements Serializable {
    @Id
    @Column(name = "student_number", nullable = false, length = 20)
    private String studentNumber;

    @Id
    @Column(name = "subject_name", nullable = false, length = 50)
    private String subjectName;

    @Id
    @Column(name = "academic_year", nullable = false, length = 20)
    private String academicYear;

    @Id
    @Column(name = "semester", nullable = false, length = 10)
    private String semester;

    @Column(name = "score", nullable = false, precision = 5, scale = 2)
    private BigDecimal score;

    @Column(name = "credit", precision = 3, scale = 1)
    private BigDecimal credit;

    public TempAcademicRecord() {
    }

    // Getters and Setters
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
}
