package com.example.mia_hometest.common;

public class ThemeItem implements DisplayItem{
    String title;
    int color;
    public ThemeItem (String title, int color) {
        this.title = title;
        this.color = color;
    }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public int getColor() {
        return color;
    }

    @Override
    public String getDisplayTitle() {
        return title;
    }
}
