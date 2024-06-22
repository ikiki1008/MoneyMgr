package com.example.mia_hometest.common;

public class SpecificListItem {
    String id;
    String inorout;
    String date;
    String amount;
    String category;
    String account;
    String note;

    public SpecificListItem(String id, String inorout, String date, String amount,
                            String category, String account, String note) {
        this.id = id;
        this.inorout = inorout;
        this.date = date;
        this.amount = amount;
        this.category = category;
        this.account = account;
        this.note = note;
    }

    public String getId() { return id; }
    public String getDate() {
        return date;
    }
    public String getInorOut () {
        return inorout;
    }
    public String getAmount() {
        return amount;
    }
    public String getCategory() { return category; }
    public String getAccount() { return account; }
    public String getNote() { return note; }


    public void setInorOut (String inorOut) {
        this.inorout = inorOut;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }
    public void setCategory(String category) {
        this.category = category;
    }
    public void setDate(String date) {
        this.date = date;
    }
    public void setAccount(String account) {
        this.account = account;
    }
    public void setNote(String note) {
        this.note = note;
    }
}
