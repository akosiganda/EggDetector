package com.example.eggdetector.models;

public class AddedReportModel {
    private String eggQuality;
    private int count;

    public AddedReportModel(String eggQuality, int count) {
        this.eggQuality = eggQuality;
        this.count = count;
    }

    public String getEggQuality() {
        return eggQuality;
    }

    public void setEggQuality(String eggQuality) {
        this.eggQuality = eggQuality;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public void increaseCount() {
        this.count += 1;
    }

    public void decreaseCount() {
        this.count -= 1;
    }
}
