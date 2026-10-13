package com.example.smartcampusrecruitment.model;

public class ContactItem {
    private String name;
    private String touxiang;
    private String company;
    private String school;
    private String phone;

    public ContactItem(String name, String touxiang, String company, String school, String phone) {
        this.name = name;
        this.touxiang = touxiang;
        this.company = company;
        this.school = school;
        this.phone = phone;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTouxiang() {
        return touxiang;
    }

    public void setTouxiang(String touxiang) {
        this.touxiang = touxiang;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getSchool() {
        return school;
    }

    public void setSchool(String school) {
        this.school = school;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}
