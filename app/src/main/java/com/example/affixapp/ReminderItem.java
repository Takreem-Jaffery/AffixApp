package com.example.affixapp;

public class ReminderItem {
    String date;

    String time;
    String name;
    String description;

    String userID;

    public ReminderItem(String date, String time, String name, String description) {
        this.date = date;
        this.time = time;
        this.name = name;
        this.description = description;

    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getUserID() {
        return userID;
    }


}