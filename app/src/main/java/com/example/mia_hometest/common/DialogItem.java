package com.example.mia_hometest.common;

public class DialogItem {
    String title;
    String desc;
    private boolean visible;

    public DialogItem(String title, String desc) {
        this.title = title;
        this.desc = desc;
        this.visible = true;
    }

    public String getTitle() {
        return title;
    }

    public String getDesc() {
        return desc;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public boolean isVisible() {
        return visible;
    }

    public void setVisible (boolean visible) {
        this.visible = visible;
    }
}
