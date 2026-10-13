package com.example.demo.model;

public class EducationItem {
    private String school;
    private String major;
    private String startDate;
    private String endDate;

    public EducationItem() {
    }

    public EducationItem(String school, String major, String startDate, String endDate) {
        this.school = school;
        this.major = major;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public String getSchool() {
        return school;
    }

    public void setSchool(String school) {
        this.school = school;
    }

    public String getMajor() {
        return major;
    }

    public void setMajor(String major) {
        this.major = major;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }
}
