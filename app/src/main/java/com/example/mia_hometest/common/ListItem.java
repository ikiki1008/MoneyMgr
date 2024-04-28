package com.example.mia_hometest.common;

public class ListItem {
    String title;
    String price;
    String date;

    public ListItem (String title, String price, String date) {
        this.title = title;
        this.price = price;
        this.date = date;
    }

    public String getTitle() {
        return title;
    }
    public String getPrice() {
        return price;
    }
    public String getDate() {
        return date;
    }

    public void setTitle(String title) {
        this.title = title;
    }
    public void setPrice(String price) {
        this.price = price;
    }
    public void setDate(String date) {
        this.date = date;
    }
}
