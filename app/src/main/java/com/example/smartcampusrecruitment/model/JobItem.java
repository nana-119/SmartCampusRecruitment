package com.example.smartcampusrecruitment.model;

public class JobItem {
    public String position;   // 职位
    public String cities;     // 城市（已用“/”拼接）
    public String salary;     // 薪资范围

    public JobItem(String position, String cities, String salary) {
        this.position = position;
        this.cities = cities;
        this.salary = salary;
    }
}

