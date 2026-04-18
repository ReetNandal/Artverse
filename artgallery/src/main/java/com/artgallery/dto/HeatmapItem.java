package com.artgallery.dto;

public class HeatmapItem {
    private String title;
    private double percentage;
    private String cssClass;

    public HeatmapItem(String title, double percentage, String cssClass) {
        this.title = title;
        this.percentage = percentage;
        this.cssClass = cssClass;
    }

    public String getTitle() {
        return title;
    }

    public double getPercentage() {
        return percentage;
    }

    public String getCssClass() {
        return cssClass;
    }
}