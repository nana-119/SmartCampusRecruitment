package com.example.demo.model;

import java.io.Serializable;
import java.util.Objects;

public class AcademicRecordId implements Serializable {
    private String studentNumber;
    private String subjectName;
    private String academicYear;
    private String semester;

    public AcademicRecordId(){
    }

    public AcademicRecordId(String studentNumber, String subjectName, String academicYear, String semester){
        this.studentNumber = studentNumber;
        this.subjectName = subjectName;
        this.academicYear = academicYear;
        this.semester = semester;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AcademicRecordId that = (AcademicRecordId) o;
        return  Objects.equals(studentNumber, that.studentNumber) &&
                Objects.equals(subjectName, that.subjectName) &&
                Objects.equals(academicYear, that.academicYear) &&
                Objects.equals(semester, that.semester);
    }

    @Override
    public int hashCode() {
        return Objects.hash(studentNumber, subjectName, academicYear, semester);
    }
}
