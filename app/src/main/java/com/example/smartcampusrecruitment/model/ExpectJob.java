package com.example.smartcampusrecruitment.model;

public class ExpectJob {
    private String name;
    private boolean selected;
    public ExpectJob(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }
}
