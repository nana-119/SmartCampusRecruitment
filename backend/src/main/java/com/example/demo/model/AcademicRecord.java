package com.example.demo.model;

import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "academic_record")
@IdClass(AcademicRecordId.class)
public class AcademicRecord  implements Serializable {

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

    public AcademicRecord() {
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
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AcademicRecord that = (AcademicRecord) o;
        return Objects.equals(studentNumber, that.studentNumber) &&
                Objects.equals(subjectName, that.subjectName) &&
                Objects.equals(academicYear, that.academicYear) &&
                Objects.equals(semester, that.semester);
    }

    @Override
    public int hashCode() {
        return Objects.hash(studentNumber, subjectName, academicYear, semester);
    }
}
