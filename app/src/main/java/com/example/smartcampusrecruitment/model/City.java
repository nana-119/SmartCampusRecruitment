package com.example.smartcampusrecruitment.model;

public class City {
    private String name;   // 城市名
    private String pinyin; // 拼音
    private boolean selected;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPinyin() {
        return pinyin;
    }

    public void setPinyin(String pinyin) {
        this.pinyin = pinyin;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public City(String name, String pinyin) {
        this.name = name;
        this.pinyin = pinyin;
    }
}
