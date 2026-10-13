package com.example.demo.model;

public class ExperienceItem {
    private String company;
    private String position;
    private String startDate;
    private String endDate;
    private String description;

    public ExperienceItem() {
    }

    public ExperienceItem(String company, String position, String startDate, String endDate, String description) {
        this.company = company;
        this.position = position;
        this.startDate = startDate;
        this.endDate = endDate;
        this.description = description;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
