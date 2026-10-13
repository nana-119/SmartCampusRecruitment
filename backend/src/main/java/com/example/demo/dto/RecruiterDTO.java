package com.example.demo.dto;

import com.example.demo.model.Recruiter;

public class RecruiterDTO {
    private String phone;
    private String password;
    private String name;
    private Recruiter.Gender gender;
    private String company;
    private String position;
    private String touxiang;

    public RecruiterDTO() {
    }

    public RecruiterDTO(String phone, String password, String name, Recruiter.Gender gender, String company, String position, String touxiang) {
        this.phone = phone;
        this.password = password;
        this.name = name;
        this.gender = gender;
        this.company = company;
        this.position = position;
        this.touxiang = touxiang;
    }


    // Getters and Setters

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Recruiter.Gender getGender() {
        return gender;
    }

    public void setGender(Recruiter.Gender gender) {
        this.gender = gender;
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

    public String getTouxiang() {
        return touxiang;
    }

    public void setTouxiang(String touxiang) {
        this.touxiang = touxiang;
    }
}
