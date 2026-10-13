package com.example.demo.dto;

public class PositionStatDTO {
    private String positionName; // 岗位名称
    private int count; // 数量（发布量/投递量）

    public PositionStatDTO(String positionName, int count) {
        this.positionName = positionName;
        this.count = count;
    }

    // getter和setter
    public String getPositionName() { return positionName; }
    public void setPositionName(String positionName) { this.positionName = positionName; }
    public int getCount() { return count; }
    public void setCount(int count) { this.count = count; }
}
