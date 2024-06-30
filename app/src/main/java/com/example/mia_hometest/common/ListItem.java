package com.example.mia_hometest.common;

import android.graphics.drawable.Drawable;

public class ListItem {
    Drawable image;
    String id;
    String title;
    String price;
    String date;

    public ListItem (Drawable image, String id, String title, String price, String date) {
        this.image = image;
        this.id = id;
        this.title = title;
        this.price = price;
        this.date = date;
    }

    public Drawable getImage() { return image; }
    public String getId() { return id; }
    public String getTitle() {
        return title;
    }
    public String getPrice() {
        return price;
    }
    public String getDate() {
        return date;
    }

    public void setImage(Drawable image) { this.image = image; }
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
