package com.example.smartcampusrecruitment.model;

public class JobWrapper {
    public String key;
    public JobItem job;
    public JobWrapper(String key, JobItem job) {
        this.key = key;
        this.job = job;
    }
}
