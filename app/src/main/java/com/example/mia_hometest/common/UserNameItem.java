package com.example.mia_hometest.common;

public class UserNameItem implements DisplayItem {
    String name;
    public UserNameItem(String name) {
        this.name = name;
    }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    @Override
    public String getDisplayTitle() {
        return name;
    }
}
